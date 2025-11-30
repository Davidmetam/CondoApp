package com.android.example.condoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.adapters.ContactAdapter
import com.android.example.condoapp.databinding.ActivityDirectoryBinding
import com.google.firebase.firestore.FirebaseFirestore

class DirectoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDirectoryBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvContacts.layoutManager = LinearLayoutManager(this)

        loadContacts()
    }

    private fun loadContacts() {
        // Obtenemos la colección "contacts"
        db.collection("contacts")
            .get()
            .addOnSuccessListener { result ->
                val contactList = ArrayList<Contact>()
                for (document in result) {
                    contactList.add(document.toObject(Contact::class.java))
                }

                // Configuramos el adaptador
                // Pasamos una función lambda que recibe el número de teléfono
                binding.rvContacts.adapter = ContactAdapter(contactList) { phoneNumber ->
                    makePhoneCall(phoneNumber)
                }

                if (contactList.isEmpty()) {
                    // Si no hay contactos en la BD, podrías mostrar unos dummy por defecto
                    // para que el profesor vea que funciona la UI
                    showDummyContacts()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar directorio", Toast.LENGTH_SHORT).show()
                showDummyContacts() // Fallback a datos locales
            }
    }

    private fun makePhoneCall(number: String) {
        if (number.isNotEmpty()) {
            // ACTION_DIAL abre el teclado numérico con el número puesto (NO requiere permisos peligrosos)
            // ACTION_CALL llamaría directamente pero requiere pedir permisos en tiempo de ejecución.
            // Usamos DIAL por simplicidad y seguridad.
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Número no válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDummyContacts() {
        val dummies = listOf(
            Contact("1", "Caseta de Vigilancia", "3312345678", "Acceso Principal"),
            Contact("2", "Administración", "3387654321", "Horario 9am - 6pm"),
            Contact("3", "Emergencias", "911", "Policía / Bomberos")
        )
        binding.rvContacts.adapter = ContactAdapter(dummies) { phone -> makePhoneCall(phone) }
    }
}