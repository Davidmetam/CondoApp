package com.android.example.condoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Inicializar el fragmento del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Coordenadas simuladas (Basadas en Guadalajara o genéricas)
        // Puedes cambiar estos números por las coordenadas reales de tus pines
        val pin1 = LatLng(20.676150, -103.389920) // Ejemplo Pin 0
        val pin2 = LatLng(20.675500, -103.390500) // Ejemplo Pin 1

        mMap.addMarker(MarkerOptions().position(pin1).title("Punto de Encuentro (0)"))
        mMap.addMarker(MarkerOptions().position(pin2).title("Área Común (1)"))

        // Mover la cámara al primer pin
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pin1, 17f))
    }
}