package com.example.notasapp.model

data class LoginRequest(val email: String, val password: String)

data class LoginRespuesta(
    val mensaje: String,
    val token: String?,
    val usuario: Usuario?
)
