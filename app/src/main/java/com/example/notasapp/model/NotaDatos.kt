package com.example.notasapp.model

data class CrearNotaRequest(val titulo: String, val contenido: String)

data class GuardarNotaRespuesta(val mensaje: String, val nota: Nota?)

data class NotasRespuesta(val mensaje: String, val notas: List<Nota>?)
