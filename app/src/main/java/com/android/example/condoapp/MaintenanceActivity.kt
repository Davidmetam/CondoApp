package com.android.example.condoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.adapters.TicketAdapter
import com.android.example.condoapp.databinding.ActivityMaintenanceBinding

class MaintenanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaintenanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tickets = listOf(
            Ticket("Fuga de agua en baño", "En Progreso", "25 Oct", "Se detectó gotera constante en lavabo principal."),
            Ticket("Luz pasillo fundida", "Reportado", "26 Oct", "Lámpara frente al 101 no enciende.")
        )

        binding.rvTickets.layoutManager = LinearLayoutManager(this)
        binding.rvTickets.adapter = TicketAdapter(tickets)
    }
}