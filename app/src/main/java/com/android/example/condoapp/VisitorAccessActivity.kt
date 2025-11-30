package com.android.example.condoapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.example.condoapp.databinding.ActivityVisitorAccessBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VisitorAccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVisitorAccessBinding

    // Inicializar Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitorAccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGenerateQr.setOnClickListener {
            val visitorName = binding.tilVisitorName.editText?.text.toString()

            if (visitorName.isNotEmpty()) {
                registerVisitAndShowQR(visitorName)
            } else {
                Toast.makeText(this, "Por favor ingresa un nombre", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerVisitAndShowQR(name: String) {
        val userId = auth.currentUser?.uid ?: return

        // 1. Crear datos de la visita
        // Generamos el ID manual para poder usarlo en el QR antes de guardar
        val newVisitRef = db.collection("visits").document()
        val visitId = newVisitRef.id

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val visit = Visit(
            id = visitId,
            hostId = userId,
            visitorName = name,
            creationDate = currentDate,
            status = "Activa"
        )

        // 2. Guardar en Firestore
        newVisitRef.set(visit)
            .addOnSuccessListener {
                Toast.makeText(this, "¡Visita registrada!", Toast.LENGTH_SHORT).show()

                // 3. Generar el QR con el ID de la visita
                // El guardia escanearía este código y buscaría el ID en la BD
                // 3. Generar el QR con el ID de la visita
                val qrBitmap = generateQRCode(visitId)

                if (qrBitmap != null) {
                    // Cargar el bitmap
                    binding.imgQrPlaceholder.setImageBitmap(qrBitmap)

                    // --- CORRECCIÓN PARA EL CUADRO AZUL ---
                    // Forzamos la eliminación de cualquier tinte (tint) que tenga el XML
                    binding.imgQrPlaceholder.clearColorFilter()
                    binding.imgQrPlaceholder.imageTintList = null
                    // --------------------------------------
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
    }

    // Función auxiliar para convertir texto a imagen QR
    private fun generateQRCode(text: String): Bitmap? {
        val width = 500
        val height = 500
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height
            )
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}