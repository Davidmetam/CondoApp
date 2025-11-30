package com.android.example.condoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.adapters.TicketAdapter
import com.android.example.condoapp.databinding.ActivityMaintenanceBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class MaintenanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaintenanceBinding

    // Inicializar Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTickets.layoutManager = LinearLayoutManager(this)

        // 1. Cargar reportes desde Firebase
        listenForMyTickets()

        // 2. Configurar botón de reporte
        binding.fabReport.setOnClickListener {
            showReportDialog()
        }
    }

    private fun listenForMyTickets() {
        val userId = auth.currentUser?.uid ?: return

        // Escuchamos la colección "incidents" filtrando por el usuario actual
        db.collection("incidents")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error cargando reportes", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val list = ArrayList<Ticket>()
                for (doc in snapshots!!) {
                    list.add(doc.toObject(Ticket::class.java))
                }

                // Si la lista está vacía, podrías mostrar un texto de "No hay reportes"
                binding.rvTickets.adapter = TicketAdapter(list)
            }
    }

    private fun showReportDialog() {
        val builder = AlertDialog.Builder(this)

        // Inflamos el diseño personalizado que creamos
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_ticket, null)
        val etTitle = view.findViewById<EditText>(R.id.et_ticket_title)
        val etDesc = view.findViewById<EditText>(R.id.et_ticket_desc)

        builder.setView(view)
        builder.setPositiveButton("Enviar Reporte") { dialog, _ ->
            val title = etTitle.text.toString()
            val desc = etDesc.text.toString()

            if (title.isNotEmpty() && desc.isNotEmpty()) {
                saveTicketToFirestore(title, desc)
            } else {
                Toast.makeText(this, "Por favor completa la información", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun saveTicketToFirestore(title: String, description: String) {
        val userId = auth.currentUser?.uid ?: return

        // Generamos referencia nueva para obtener el ID automático
        val newDoc = db.collection("incidents").document()

        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        val date = sdf.format(Date())

        val ticket = Ticket(
            id = newDoc.id,
            userId = userId,
            title = title,
            description = description,
            date = date,
            status = "Reportado"
        )

        newDoc.set(ticket)
            .addOnSuccessListener {
                Toast.makeText(this, "Reporte enviado a Administración", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show()
            }
    }
}