package com.example.notasapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notasapp.model.Usuario
import com.example.notasapp.repository.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import com.example.notasapp.network.ErroresApi

sealed class EstadoLogin {
    data object Cargando : EstadoLogin()
    data class Correcto(val token: String, val usuario: Usuario) : EstadoLogin()
    data class Error(val mensaje: String) : EstadoLogin()
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _estado = MutableLiveData<EstadoLogin>()
    val estado: LiveData<EstadoLogin> = _estado

    fun iniciarSesion(email: String, password: String) {
        if (_estado.value is EstadoLogin.Cargando) return
        if (email.isBlank() || password.isBlank()) {
            _estado.value = EstadoLogin.Error("Ingresa tu correo y contraseña")
            return
        }
        _estado.value = EstadoLogin.Cargando
        viewModelScope.launch {
            try {
                val respuesta = repository.iniciarSesion(email.trim(), password)
                val datos = respuesta.body()
                _estado.value = when {
                    respuesta.isSuccessful && datos != null && !datos.token.isNullOrBlank() && datos.usuario != null ->
                        EstadoLogin.Correcto(datos.token, datos.usuario)
                    respuesta.isSuccessful -> EstadoLogin.Error("La respuesta de login está incompleta")
                    respuesta.code() == 401 ->
                        EstadoLogin.Error("Correo o contraseña incorrectos")
                    else -> EstadoLogin.Error(ErroresApi.mensaje(respuesta))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _estado.value = EstadoLogin.Error(ErroresApi.mensaje(error))
            }
        }
    }
}

class LoginViewModelFactory(private val repo: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repo) as T
        }
        throw IllegalArgumentException("ViewModel no reconocido")
    }
}
