package com.example.notasapp.network

import com.example.notasapp.model.CrearNotaRequest
import com.example.notasapp.model.GuardarNotaRespuesta
import com.example.notasapp.model.LoginRequest
import com.example.notasapp.model.LoginRespuesta
import com.example.notasapp.model.NotasRespuesta
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body datos: LoginRequest): Response<LoginRespuesta>

    @POST("api/notas")
    suspend fun guardarNota(@Body datos: CrearNotaRequest): Response<GuardarNotaRespuesta>

    @GET("api/notas")
    suspend fun consultarNotas(): Response<NotasRespuesta>
}
