package com.android.example.condoapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.example.condoapp.databinding.ActivityVisitorAccessBinding

class VisitorAccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVisitorAccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitorAccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGenerateQr.setOnClickListener {
            val name = binding.tilVisitorName.editText?.text.toString()
            if (name.isNotEmpty()) {
                // Simulación de generación
                binding.imgQrPlaceholder.setImageResource(R.drawable.ic_launcher_background) // Cambiar por imagen QR real si tuvieras
                binding.imgQrPlaceholder.setColorFilter(getColor(R.color.black))
                Toast.makeText(this, "QR Generado para $name", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ingresa un nombre", Toast.LENGTH_SHORT).show()
            }
        }
    }
}