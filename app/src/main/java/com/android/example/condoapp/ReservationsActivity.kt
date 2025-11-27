package com.android.example.condoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.adapters.ReservationAdapter
import com.android.example.condoapp.databinding.ActivityReservationsBinding

class ReservationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reservations = listOf(
            Reservation("Terraza A", "30 Oct 2025, 14:00", "Aprobada"),
            Reservation("Salón Usos Múltiples", "15 Nov 2025, 20:00", "Pendiente")
        )

        binding.rvMyReservations.layoutManager = LinearLayoutManager(this)
        binding.rvMyReservations.adapter = ReservationAdapter(reservations)
    }
}