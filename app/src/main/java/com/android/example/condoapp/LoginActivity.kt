package com.android.example.condoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.example.condoapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
// Asegúrate de que este import apunte a tu nuevo archivo Keys
import com.android.example.condoapp.Keys

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // 1. Inicializar Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Auth
        auth = Firebase.auth

        // Configurar Google Sign In
        setupGoogleSignIn()

        // Botón Login con Correo/Contraseña
        binding.btnLogin.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginWithEmail(email, password)
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Login con Google
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Botón de Registro
        binding.tvRegister.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerWithEmail(email, password)
            } else {
                Toast.makeText(this, "Ingresa correo y contraseña para registrarte", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Verificar si el usuario ya está logueado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMain()
        }
    }

    private fun loginWithEmail(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Bienvenido de nuevo", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(baseContext, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerWithEmail(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Crear objeto usuario con datos básicos
                    // Nota: Asegúrate de haber agregado la data class User en Models.kt
                    val newUser = User(
                        uid = user!!.uid,
                        email = email,
                        nombreCompleto = "Usuario Nuevo", // Valor temporal hasta que editen perfil
                        rol = "Residente",
                        estado = "Pendiente"
                    )

                    saveUserToFirestore(newUser)
                } else {
                    Toast.makeText(baseContext, "Fallo el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // --- Lógica de Google Sign-In ---

    private fun setupGoogleSignIn() {
        // Usamos la constante desde Keys.kt
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Keys.WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("LoginActivity", "Google sign in failed", e)
                Toast.makeText(this, "Google Sign In falló", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    // Verificamos si el usuario ya existe en Firestore antes de sobrescribirlo
                    checkUserExists(firebaseUser!!)
                } else {
                    Toast.makeText(this, "Error de autenticación con Firebase", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // --- Lógica de Firestore ---

    private fun checkUserExists(firebaseUser: com.google.firebase.auth.FirebaseUser) {
        val docRef = db.collection("users").document(firebaseUser.uid)
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // El usuario ya existe, solo entramos
                navigateToMain()
            } else {
                // Es la primera vez que entra con Google, lo registramos en la BD
                val newUser = User(
                    uid = firebaseUser.uid,
                    nombreCompleto = firebaseUser.displayName ?: "Usuario Google",
                    email = firebaseUser.email ?: "",
                    rol = "Residente",
                    estado = "Pendiente"
                )
                saveUserToFirestore(newUser)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al verificar usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserToFirestore(user: User) {
        db.collection("users").document(user.uid).set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro completado exitosamente", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        // Limpiar el stack para que no pueda volver al login con "Atrás"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}