package com.android.example.condoapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.example.condoapp.databinding.ActivitySosBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

// Agregamos SensorEventListener a la clase
class SosActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivitySosBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    // Variables para el sensor
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0 // Para evitar múltiples envíos en un solo movimiento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Inicializar el Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Toast.makeText(this, "Tu dispositivo no tiene acelerómetro", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Agita el teléfono para enviar alerta", Toast.LENGTH_SHORT).show()
        }

        // Configuración del botón manual (también sigue funcionando)
        binding.btnSendSos.setOnClickListener {
            sendSosAlert("Botón Manual")
        }
    }

    // 2. Registrar el sensor cuando la actividad está visible
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // 3. Pausar el sensor cuando salimos (para ahorrar batería)
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // 4. Lógica de detección de movimiento
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Cálculo de la fuerza G (Gravedad)
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // Fórmula: sqrt(x² + y² + z²)
            // Una fuerza G de 1.0 es estar quieto. Agitarlo fuerte sube este valor.
            val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

            // UMBRAL DE AGITACIÓN: 2.7G (Ajustable. Más alto = hay que agitar más fuerte)
            if (gForce > 2.7f) {
                val now = System.currentTimeMillis()
                // Evitar rebotes: Solo permitir una alerta cada 3 segundos
                if (lastShakeTime + 3000 < now) {
                    lastShakeTime = now

                    // ¡AGITACIÓN DETECTADA! -> ENVIAR ALERTA
                    if (binding.btnSendSos.isEnabled) { // Solo si no se está enviando ya
                        Toast.makeText(this, "¡Movimiento Brusco Detectado!", Toast.LENGTH_SHORT).show()
                        sendSosAlert("Sensor Acelerómetro")
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementar esto
    }

    // Función de envío (modificada para recibir el tipo de trigger)
    private fun sendSosAlert(triggerSource: String) {
        binding.progressSos.visibility = View.VISIBLE
        binding.btnSendSos.isEnabled = false

        val userId = auth.currentUser?.uid ?: return
        val userName = auth.currentUser?.displayName ?: "Residente"

        val newDoc = db.collection("sos_alerts").document()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        val alert = SosAlert(
            id = newDoc.id,
            userId = userId,
            userName = userName,
            date = sdf.format(Date()),
            type = "Emergencia (Vía $triggerSource)", // Guardamos cómo se activó
            status = "Activa"
        )

        newDoc.set(alert)
            .addOnSuccessListener {
                binding.progressSos.visibility = View.GONE
                Toast.makeText(this, "ALERTA ENVIADA A CASETA", Toast.LENGTH_LONG).show()

                // Cerramos después de 2 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 2000)
            }
            .addOnFailureListener {
                binding.progressSos.visibility = View.GONE
                binding.btnSendSos.isEnabled = true
                Toast.makeText(this, "Error al enviar alerta", Toast.LENGTH_SHORT).show()
            }
    }
}