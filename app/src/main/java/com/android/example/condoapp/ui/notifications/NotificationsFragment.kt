package com.android.example.condoapp.ui.notifications

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.android.example.condoapp.Keys
import com.android.example.condoapp.LoginActivity
import com.android.example.condoapp.R
import com.android.example.condoapp.User
import com.android.example.condoapp.databinding.FragmentNotificationsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage

    // Variables para la selección de imagen
    private var imageUri: Uri? = null
    private var tempImageView: ImageView? = null // Referencia temporal para mostrar preview en el diálogo

    // Lanzador para abrir la galería
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            // Mostrar la imagen seleccionada en el diálogo usando Coil
            tempImageView?.load(uri) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar datos iniciales
        loadProfileInfo()

        // Botón Editar Perfil
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        // Botón Cerrar Sesión
        binding.tvLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun loadProfileInfo() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).addSnapshotListener { document, _ ->
            if (document != null && document.exists()) {
                val user = document.toObject(User::class.java)
                binding.tvProfileName.text = user?.nombreCompleto ?: "Usuario"

                // Cargar imagen de perfil con Coil
                if (!user?.photoUrl.isNullOrEmpty()) {
                    binding.imgProfile.load(user?.photoUrl) {
                        crossfade(true)
                        // Si falla o está cargando, mostramos un color o imagen base
                        error(R.drawable.ic_launcher_background)
                    }
                }
            }
        }
    }

    private fun showEditProfileDialog() {
        val userId = auth.currentUser?.uid ?: return

        // 1. Inflar el diseño del diálogo
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_edit_name)
        val etPhone = dialogView.findViewById<EditText>(R.id.et_edit_phone)
        val imgAvatar = dialogView.findViewById<ImageView>(R.id.img_edit_avatar)

        tempImageView = imgAvatar // Guardar referencia para actualizarla al elegir foto
        imageUri = null // Reiniciar selección

        // 2. Pre-cargar datos actuales
        db.collection("users").document(userId).get().addOnSuccessListener { doc ->
            val user = doc.toObject(User::class.java)
            etName.setText(user?.nombreCompleto)
            etPhone.setText(user?.phone)

            // Cargar foto actual en el diálogo si existe
            if (!user?.photoUrl.isNullOrEmpty()) {
                imgAvatar.load(user?.photoUrl) { transformations(CircleCropTransformation()) }
            }
        }

        // 3. Configurar clic en la imagen para abrir galería
        imgAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 4. Mostrar el diálogo
        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val newName = etName.text.toString()
                val newPhone = etPhone.text.toString()

                if (newName.isNotEmpty()) {
                    if (imageUri != null) {
                        uploadImageAndSaveProfile(userId, newName, newPhone, imageUri!!)
                    } else {
                        saveProfileData(userId, newName, newPhone, null)
                    }
                } else {
                    Toast.makeText(context, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun uploadImageAndSaveProfile(userId: String, name: String, phone: String, uri: Uri) {
        Toast.makeText(context, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        // Referencia: profile_images/UID.jpg
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveProfileData(userId, name, phone, downloadUrl.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileData(userId: String, name: String, phone: String, photoUrl: String?) {
        val updates = hashMapOf<String, Any>(
            "nombreCompleto" to name,
            "phone" to phone
        )
        if (photoUrl != null) {
            updates["photoUrl"] = photoUrl
        }

        db.collection("users").document(userId).update(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun performLogout() {
        auth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Keys.WEB_CLIENT_ID)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut().addOnCompleteListener {
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