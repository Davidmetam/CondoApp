package com.android.example.condoapp.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.example.condoapp.Keys
import com.android.example.condoapp.LoginActivity
import com.android.example.condoapp.User
import com.android.example.condoapp.databinding.FragmentNotificationsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    // Inicializar Firebase
    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Cargar datos del perfil
        loadProfileInfo()

        // 2. Configurar botón de Cerrar Sesión
        binding.tvLogout.setOnClickListener {
            performLogout()
        }

        // Simulación de editar foto
        binding.tvProfileName.setOnClickListener {
            Toast.makeText(context, "Próximamente: Editar Perfil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileInfo() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        binding.tvProfileName.text = user?.nombreCompleto ?: "Usuario"
                        // Si tuvieras un ImageView real para la foto, aquí cargarías la URL con Glide o Picasso
                    }
                }
        }
    }

    private fun performLogout() {
        // 1. Cerrar sesión en Firebase
        auth.signOut()

        // 2. Cerrar sesión en el cliente de Google (importante para poder cambiar de cuenta)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Keys.WEB_CLIENT_ID)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut().addOnCompleteListener {
            // 3. Redirigir al Login y limpiar el historial de navegación
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}