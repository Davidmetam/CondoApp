package com.android.example.condoapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.Announcement
import com.android.example.condoapp.adapters.AnnouncementAdapter
import com.android.example.condoapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyData = listOf(
            Announcement("Mantenimiento Elevadores", "26 Oct, 10:00 AM", "Se realizará mantenimiento preventivo en la Torre B.", "Aviso"),
            Announcement("Reunión de Vecinos", "28 Oct, 18:00 PM", "Junta anual para revisar presupuestos 2026.", "Evento"),
            Announcement("Fumigación", "30 Oct, 09:00 AM", "Favor de mantener cerradas las ventanas.", "Servicio")
        )

        binding.rvAnnouncements.layoutManager = LinearLayoutManager(context)
        binding.rvAnnouncements.adapter = AnnouncementAdapter(dummyData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}