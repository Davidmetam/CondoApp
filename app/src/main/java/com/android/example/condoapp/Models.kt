package com.android.example.condoapp
// Agregamos valores por defecto (= "") para que Firestore pueda crear el objeto
data class Announcement(
    val id: String = "", // Agregamos ID por si necesitamos editar/borrar luego
    val title: String = "",
    val date: String = "", // Guardaremos la fecha como texto formateado por simplicidad
    val content: String = "",
    val type: String = "Aviso"
)

data class Transaction(
    val concept: String,
    val date: String,
    val amount: String,
    val isPositive: Boolean
)

data class Reservation(
    val id: String = "",
    val userId: String = "", // Para saber de quién es
    val areaName: String = "",
    val dateTime: String = "",
    val status: String = "Pendiente" // Pendiente, Aprobada, Rechazada
)

// ... otras clases ...

data class Ticket(
    val id: String = "",
    val userId: String = "", // Para saber quién reportó
    val title: String = "",
    val status: String = "Abierto", // Abierto, En Progreso, Cerrado
    val date: String = "",
    val description: String = ""
)
data class User(
    val uid: String = "",
    val nombreCompleto: String = "",
    val email: String = "",
    val rol: String = "Residente", // Por defecto
    val idDepartamento: String? = null,
    val estado: String = "Pendiente" // Para la aprobación que menciona el PDF
)
data class Visit(
    val id: String = "",
    val hostId: String = "",       // ID del residente que invita
    val visitorName: String = "",
    val creationDate: String = "",
    val status: String = "Activa"  // Activa, Completada, Expirada
)
data class Contact(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "", // Número de teléfono
    val description: String = "", // Ej: "Guardia Turno Nocturno"
    val iconName: String = "default" // Para poner íconos distintos si quisiéramos luego
)