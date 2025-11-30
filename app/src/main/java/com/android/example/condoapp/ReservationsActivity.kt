package com.android.example.condoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.adapters.ReservationAdapter
import com.android.example.condoapp.databinding.ActivityReservationsBinding
import com.google.android.material.chip.Chip
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservationsBinding

    // Inicializar Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        binding.rvMyReservations.layoutManager = LinearLayoutManager(this)

        // 1. Cargar las reservaciones del usuario
        listenForMyReservations()

        // 2. Configurar el botón flotante para CREAR una nueva reserva
        binding.root.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_reservation)
            ?.setOnClickListener { // Nota: Si el ID no está en el XML, usa el genérico o agrégalo.
                createReservationSimulator()
            }

        // Si el FAB no tenía ID en el XML original, usaremos el binding directo si es posible
        // O buscamos la vista genéricamente si no tiene ID específico en tu layout anterior
        // Revisando tu layout, parece que el FAB no tenía ID.
        // Vamos a asignarle la acción a la vista que encontremos.
        val fab = binding.root.findViewWithTag<android.view.View>("fab_action")
        // Como no pusimos tag, mejor buscamos por tipo o asignamos el click listener dinámicamente si tienes acceso al XML.
        // HACK RÁPIDO: Asignar OnClickListener a todos los FABs que encuentre (solo hay uno)
        /*
           NOTA PARA TI: Para que esto funcione perfecto, ve a `activity_reservations.xml`
           y agrega android:id="@+id/fab_add" al FloatingActionButton.
           Mientras tanto, usaré un método seguro.
        */
    }

    // Esta función se llamará cuando arregles el ID del FAB, o intenta vincularlo así:
    override fun onStart() {
        super.onStart()
        // Buscamos el FAB manualmente si no tiene ID en el binding generado
        val childCount = binding.root.childCount
        for (i in 0 until childCount) {
            val view = binding.root.getChildAt(i)
            if (view is com.google.android.material.floatingactionbutton.FloatingActionButton) {
                view.setOnClickListener { createReservationSimulator() }
            }
        }
    }

    private fun listenForMyReservations() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("reservations")
            .whereEqualTo("userId", userId) // Filtro: Solo mis reservas
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al cargar reservas", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val list = ArrayList<Reservation>()
                for (doc in snapshots!!) {
                    list.add(doc.toObject(Reservation::class.java))
                }

                binding.rvMyReservations.adapter = ReservationAdapter(list)
            }
    }

    private fun createReservationSimulator() {
        val userId = auth.currentUser?.uid ?: return

        // 1. Obtener el área seleccionada de los Chips
        val chipGroup = binding.root.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chip_group_areas)
        // Si no tienes ID en el ChipGroup en el XML, asumiremos "Terraza A" por defecto
        var areaName = "Terraza A"

        // Intento de obtener texto del chip seleccionado (Requiere IDs en XML)
        /* val selectedId = binding.chipGroup.checkedChipId
        if (selectedId != -1) {
             val chip = binding.root.findViewById<Chip>(selectedId)
             areaName = chip.text.toString()
        }
        */

        // 2. Crear fecha simulada (Mañana a las 10am)
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val newReservation = Reservation(
            id = db.collection("reservations").document().id, // Generar ID nuevo
            userId = userId,
            areaName = areaName,
            dateTime = currentDate, // En una app real, aquí abrirías un DatePicker
            status = "Pendiente"
        )

        // 3. Guardar en Firestore
        db.collection("reservations").document(newReservation.id).set(newReservation)
            .addOnSuccessListener {
                Toast.makeText(this, "¡Solicitud enviada para $areaName!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al solicitar", Toast.LENGTH_SHORT).show()
            }
    }
}