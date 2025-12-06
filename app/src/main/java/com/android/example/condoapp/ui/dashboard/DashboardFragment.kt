package com.android.example.condoapp.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.example.condoapp.AccountStatusActivity
import com.android.example.condoapp.DirectoryActivity
import com.android.example.condoapp.MaintenanceActivity
import com.android.example.condoapp.MapActivity
import com.android.example.condoapp.R
import com.android.example.condoapp.ReservationsActivity
import com.android.example.condoapp.SosActivity
import com.android.example.condoapp.VisitorAccessActivity
import com.android.example.condoapp.databinding.FragmentDashboardBinding
import com.android.example.condoapp.databinding.ItemMenuCardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Finanzas
        setupMenuCard(
            cardBinding = binding.cardFinance,
            title = "Finanzas",
            iconRes = R.drawable.ic_dashboard_black_24dp,
            activityClass = AccountStatusActivity::class.java
        )

        // 2. Reservas
        setupMenuCard(
            cardBinding = binding.cardReservations,
            title = "Reservas",
            iconRes = R.drawable.ic_home_black_24dp,
            activityClass = ReservationsActivity::class.java
        )

        // 3. Visitas
        setupMenuCard(
            cardBinding = binding.cardVisits,
            title = "Visitas",
            iconRes = R.drawable.ic_dashboard_black_24dp,
            activityClass = VisitorAccessActivity::class.java
        )

        // 4. Reportes
        setupMenuCard(
            cardBinding = binding.cardReports,
            title = "Reportes",
            iconRes = R.drawable.ic_notifications_black_24dp,
            activityClass = MaintenanceActivity::class.java
        )

        // 5. Directorio
        setupMenuCard(
            cardBinding = binding.cardDirectory,
            title = "Directorio",
            iconRes = R.drawable.ic_dashboard_black_24dp,
            activityClass = DirectoryActivity::class.java
        )

        // 6. SOS (Configuración especial visual)
        val orangeColor = ContextCompat.getColor(requireContext(), R.color.naranja_ambar)
        binding.cardSos.tvMenuTitle.text = "SOS"
        binding.cardSos.tvMenuTitle.setTextColor(orangeColor)
        binding.cardSos.iconMenu.setImageResource(R.drawable.ic_notifications_black_24dp)
        binding.cardSos.iconMenu.setColorFilter(orangeColor)
        binding.cardSos.root.setCardBackgroundColor(Color.parseColor("#FFEBEE"))

        binding.cardSos.root.setOnClickListener {
            val intent = Intent(context, SosActivity::class.java)
            startActivity(intent)
        }

        // 7. Mapa
        // IMPORTANTE: Asegúrate de haber agregado <include android:id="@+id/card_map"...> en tu XML
        // Si aún no lo agregas, comenta estas líneas para que compile mientras tanto.
        setupMenuCard(
            cardBinding = binding.cardMap,
            title = "Mapa",
            iconRes = R.drawable.ic_dashboard_black_24dp,
            activityClass = MapActivity::class.java
        )
    }

    // --- AQUÍ ESTABA EL PROBLEMA ---
    // Esta es la función que te faltaba. Debe estar DENTRO de la clase DashboardFragment,
    // pero FUERA de onViewCreated.
    private fun setupMenuCard(cardBinding: ItemMenuCardBinding, title: String, iconRes: Int, activityClass: Class<*>) {
        cardBinding.tvMenuTitle.text = title
        cardBinding.iconMenu.setImageResource(iconRes)

        cardBinding.root.setOnClickListener {
            val intent = Intent(context, activityClass)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}