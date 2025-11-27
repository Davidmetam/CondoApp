package com.android.example.condoapp.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.example.condoapp.LoginActivity
import com.android.example.condoapp.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Simulación de acciones de perfil
        binding.tvProfileName.setOnClickListener {
            Toast.makeText(context, "Editar Foto", Toast.LENGTH_SHORT).show()
        }

        // Para cerrar sesión, idealmente borrarías preferencias compartidas aquí
        val logoutView = binding.root.findViewById<View>(com.android.example.condoapp.R.id.profile_header) // O el TextView específico
        // Asumiendo que agregaste IDs a los TextViews de opciones en el XML
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}