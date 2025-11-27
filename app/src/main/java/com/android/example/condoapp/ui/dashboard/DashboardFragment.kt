package com.android.example.condoapp.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.example.condoapp.AccountStatusActivity
import com.android.example.condoapp.MaintenanceActivity
import com.android.example.condoapp.R
import com.android.example.condoapp.ReservationsActivity
import com.android.example.condoapp.VisitorAccessActivity
import com.android.example.condoapp.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenuCard(
            cardView = binding.cardFinance,
            title = "Finanzas",
            iconRes = R.drawable.ic_dashboard_black_24dp,
            activityClass = AccountStatusActivity::class.java
        )

        setupMenuCard(
            cardView = binding.cardReservations,
            title = "Reservas",
            iconRes = R.drawable.ic_home_black_24dp,
            activityClass = ReservationsActivity::class.java
        )

        setupMenuCard(
            cardView = binding.cardVisits,
            title = "Visitas",
            iconRes = R.drawable.ic_dashboard_black_24dp,
            activityClass = VisitorAccessActivity::class.java
        )

        setupMenuCard(
            cardView = binding.cardReports,
            title = "Reportes",
            iconRes = R.drawable.ic_notifications_black_24dp,
            activityClass = MaintenanceActivity::class.java
        )

        val titleDirectory = binding.cardDirectory.findViewById<TextView>(R.id.tv_menu_title)
        titleDirectory.text = "Directorio"

        binding.cardSos.setOnClickListener {
        }
    }

    private fun setupMenuCard(cardView: View, title: String, iconRes: Int, activityClass: Class<*>) {
        val tvTitle = cardView.findViewById<TextView>(R.id.tv_menu_title)
        val imgIcon = cardView.findViewById<ImageView>(R.id.icon_menu)

        tvTitle.text = title
        imgIcon.setImageResource(iconRes)

        cardView.setOnClickListener {
            val intent = Intent(context, activityClass)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}