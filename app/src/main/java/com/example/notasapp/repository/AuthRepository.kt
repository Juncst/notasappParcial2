package com.example.notasapp.repository

import com.example.notasapp.model.LoginRequest
import com.example.notasapp.model.LoginRespuesta
import com.example.notasapp.network.ApiService
import retrofit2.Response

class AuthRepository(private val api: ApiService) {
    suspend fun iniciarSesion(email: String, password: String): Response<LoginRespuesta> {
        return api.login(LoginRequest(email, password))
    }
}
