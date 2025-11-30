package com.android.example.condoapp

data class Announcement(
    val title: String,
    val date: String,
    val content: String,
    val type: String
)

data class Transaction(
    val concept: String,
    val date: String,
    val amount: String,
    val isPositive: Boolean
)

data class Reservation(
    val areaName: String,
    val dateTime: String,
    val status: String
)

data class Ticket(
    val title: String,
    val status: String,
    val date: String,
    val description: String
)
// ... tus otras data classes ...

data class User(
    val uid: String = "",
    val nombreCompleto: String = "",
    val email: String = "",
    val rol: String = "Residente", // Por defecto
    val idDepartamento: String? = null,
    val estado: String = "Pendiente" // Para la aprobación que menciona el PDF
)