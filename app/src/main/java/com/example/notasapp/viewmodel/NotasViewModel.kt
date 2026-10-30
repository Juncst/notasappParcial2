package com.example.notasapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notasapp.model.Nota
import com.example.notasapp.repository.NotasRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import com.example.notasapp.network.ErroresApi

sealed class EstadoGuardar {
    data object Cargando : EstadoGuardar()
    data object Guardada : EstadoGuardar()
    data class Error(val mensaje: String) : EstadoGuardar()
}

sealed class EstadoListado {
    data object Cargando : EstadoListado()
    data class Resultado(val notas: List<Nota>) : EstadoListado()
    data class Error(val mensaje: String) : EstadoListado()
}

class NotasViewModel(private val repository: NotasRepository) : ViewModel() {
    private val _estadoGuardar = MutableLiveData<EstadoGuardar>()
    val estadoGuardar: LiveData<EstadoGuardar> = _estadoGuardar

    private val _estadoListado = MutableLiveData<EstadoListado>()
    val estadoListado: LiveData<EstadoListado> = _estadoListado

    fun guardarNota(titulo: String, contenido: String) {
        if (_estadoGuardar.value is EstadoGuardar.Cargando) return
        if (titulo.isBlank() || contenido.isBlank()) {
            _estadoGuardar.value = EstadoGuardar.Error("Completa el título y el contenido")
            return
        }
        if (titulo.length > 200) {
            _estadoGuardar.value = EstadoGuardar.Error("El título no debe superar 200 caracteres")
            return
        }
        _estadoGuardar.value = EstadoGuardar.Cargando
        viewModelScope.launch {
            try {
                val resultado = repository.guardar(titulo.trim(), contenido.trim())
                _estadoGuardar.value = when {
                    resultado.isSuccessful && resultado.body()?.nota != null -> EstadoGuardar.Guardada
                    resultado.isSuccessful -> EstadoGuardar.Error("El servidor no devolvió la nota guardada")
                    resultado.code() == 401 || resultado.code() == 403 ->
                        EstadoGuardar.Error("Tu sesión expiró. Inicia sesión nuevamente")
                    else -> EstadoGuardar.Error(ErroresApi.mensaje(resultado))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _estadoGuardar.value = EstadoGuardar.Error(ErroresApi.mensaje(error))
            }
        }
    }

    fun consultarNotas() {
        if (_estadoListado.value is EstadoListado.Cargando) return
        _estadoListado.value = EstadoListado.Cargando
        viewModelScope.launch {
            try {
                val resultado = repository.consultar()
                val datos = resultado.body()
                _estadoListado.value = when {
                    resultado.isSuccessful && datos?.notas != null ->
                        EstadoListado.Resultado(datos.notas)
                    resultado.isSuccessful -> EstadoListado.Error("El servidor no devolvió la lista de notas")
                    resultado.code() == 401 || resultado.code() == 403 ->
                        EstadoListado.Error("Tu sesión expiró. Inicia sesión nuevamente")
                    else -> EstadoListado.Error(ErroresApi.mensaje(resultado))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _estadoListado.value = EstadoListado.Error(ErroresApi.mensaje(error))
            }
        }
    }
}

class NotasViewModelFactory(private val repo: NotasRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotasViewModel(repo) as T
        }
        throw IllegalArgumentException("ViewModel no reconocido")
    }
}
