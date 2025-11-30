package com.android.example.condoapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.Announcement
import com.android.example.condoapp.User
import com.android.example.condoapp.adapters.AnnouncementAdapter
import com.android.example.condoapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Instancias de Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView inicialmente vacío
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(context)

        // Cargar datos
        loadUserData()
        listenForAnnouncements()
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject(User::class.java)
                        binding.tvHomeWelcome.text = "Hola, ${user?.nombreCompleto ?: "Vecino"}"

                        if (user?.idDepartamento != null) {
                            binding.tvHomeUnit.text = "Unidad: ${user.idDepartamento}"
                        } else {
                            binding.tvHomeUnit.text = "Unidad: Sin asignar (Pendiente)"
                        }
                    }
                }
        }
    }

    private fun listenForAnnouncements() {
        // Escuchamos la colección "announcements" en tiempo real
        // Ordenamos por "date" descendente (lo más nuevo arriba)
        // NOTA: Si usas fechas como String, el ordenamiento alfabético podría fallar si no es YYYY-MM-DD.
        // Para producción idealmente usaríamos Timestamp, pero por ahora ordenamos por creación si fuera posible.

        db.collection("announcements")
            //.orderBy("date", Query.Direction.DESCENDING) // Descomenta esto cuando tengas índices o fechas ISO
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val announcementList = ArrayList<Announcement>()
                for (doc in snapshots!!) {
                    // Convertimos el documento a objeto
                    val anuncio = doc.toObject(Announcement::class.java)
                    // Añadimos el objeto a la lista (podríamos asignar el ID del doc al objeto aquí también)
                    announcementList.add(anuncio)
                }

                // Actualizamos el adaptador
                if (binding.rvAnnouncements.adapter == null) {
                    binding.rvAnnouncements.adapter = AnnouncementAdapter(announcementList)
                } else {
                    // Si ya existe, creamos uno nuevo o idealmente usaríamos un método update en el adapter
                    binding.rvAnnouncements.adapter = AnnouncementAdapter(announcementList)
                }

                // Mostrar mensaje si está vacío
                if (announcementList.isEmpty()) {
                    // Opcional: Mostrar un TextView de "No hay anuncios"
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}