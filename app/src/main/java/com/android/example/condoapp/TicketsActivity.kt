package com.android.example.condoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.adapters.TicketAdapter
import com.android.example.condoapp.databinding.ActivityTicketsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketsBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTickets.layoutManager = LinearLayoutManager(this)

        // 1. Cargar mis tickets existentes
        loadMyTickets()

        // 2. Botón para crear nuevo reporte
        // Asegúrate de que tu FAB en el XML tenga el ID fab_add_ticket
        binding.fabAddTicket.setOnClickListener {
            showAddTicketDialog()
        }
    }

    private fun loadMyTickets() {
        val userId = auth.currentUser?.uid ?: return

        // Escuchamos la colección "incidents" filtrando por mi usuario
        db.collection("incidents")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al cargar reportes", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val ticketList = ArrayList<Ticket>()
                for (doc in snapshots!!) {
                    ticketList.add(doc.toObject(Ticket::class.java))
                }
                // Usamos el TicketAdapter que ya tenías en tu código original
                binding.rvTickets.adapter = TicketAdapter(ticketList)
            }
    }

    // Muestra un cuadro de diálogo simple para ingresar título y descripción
    private fun showAddTicketDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nuevo Reporte")

        // Usamos un layout simple para el diálogo
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_ticket, null)
        val etTitle = view.findViewById<EditText>(R.id.et_ticket_title)
        val etDesc = view.findViewById<EditText>(R.id.et_ticket_desc)

        builder.setView(view)

        builder.setPositiveButton("Enviar") { dialog, _ ->
            val title = etTitle.text.toString()
            val desc = etDesc.text.toString()
            if (title.isNotEmpty() && desc.isNotEmpty()) {
                saveTicketToFirestore(title, desc)
            } else {
                Toast.makeText(this, "Debes llenar los campos", Toast.LENGTH_SHORT).show()
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
        val newDocRef = db.collection("incidents").document()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val newTicket = Ticket(
            id = newDocRef.id,
            userId = userId,
            title = title,
            description = description,
            date = sdf.format(Date()),
            status = "Abierto"
        )

        newDocRef.set(newTicket)
            .addOnSuccessListener {
                Toast.makeText(this, "Reporte enviado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar reporte", Toast.LENGTH_SHORT).show()
            }
    }
}