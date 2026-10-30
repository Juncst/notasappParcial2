package com.example.notasapp.repository

import com.example.notasapp.model.CrearNotaRequest
import com.example.notasapp.model.GuardarNotaRespuesta
import com.example.notasapp.model.NotasRespuesta
import com.example.notasapp.network.ApiService
import retrofit2.Response

class NotasRepository(private val api: ApiService) {
    suspend fun guardar(titulo: String, contenido: String): Response<GuardarNotaRespuesta> {
        return api.guardarNota(CrearNotaRequest(titulo, contenido))
    }

    suspend fun consultar(): Response<NotasRespuesta> {
        return api.consultarNotas()
    }
}
