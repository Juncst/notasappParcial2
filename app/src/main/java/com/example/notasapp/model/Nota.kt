package com.example.notasapp.model

data class Nota(
    val id: Int,
    val titulo: String,
    val contenido: String,
    val fecha_creacion: String? = null,
    val usuario_id: Int? = null
)
