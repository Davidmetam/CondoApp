package com.android.example.condoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.example.condoapp.adapters.TransactionAdapter
import com.android.example.condoapp.databinding.ActivityAccountStatusBinding

class AccountStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transactions = listOf(
            Transaction("Cuota Mantenimiento Oct", "01 Oct 2025", "-$1,500.00", false),
            Transaction("Pago Recibido", "05 Sep 2025", "+$1,500.00", true),
            Transaction("Cuota Mantenimiento Sep", "01 Sep 2025", "-$1,500.00", false),
            Transaction("Multa Ruido", "15 Ago 2025", "-$500.00", false)
        )

        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = TransactionAdapter(transactions)
    }
}