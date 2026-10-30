package com.example.notasapp.network

import com.google.gson.JsonParseException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErroresApi {
    fun mensaje(error: Exception): String = when (error) {
        is SocketTimeoutException -> "El servidor tardó demasiado. Revisa la conexión y el permiso de red local."
        is ConnectException -> "No se pudo conectar al servidor. Comprueba que el backend esté iniciado en el puerto 3000."
        is UnknownHostException -> "No se encontró el servidor. Revisa la conexión."
        is JsonParseException, is IllegalStateException -> "La respuesta del servidor tiene un formato inesperado."
        is SecurityException -> "Falta permitir el acceso a la red local en los ajustes de NotasApp."
        is IOException -> "No se pudo comunicar con el servidor. Revisa la conexión y el permiso de red local."
        else -> "No fue posible completar la operación. Inténtalo nuevamente."
    }

    fun mensaje(respuesta: Response<*>): String {
        if (respuesta.code() == 401 || respuesta.code() == 403) {
            return "Tu sesión expiró. Inicia sesión nuevamente"
        }
        return runCatching {
            JSONObject(respuesta.errorBody()?.string().orEmpty()).optString("mensaje")
                .takeIf { it.isNotBlank() }
        }.getOrNull() ?: "Error del servidor: ${respuesta.code()}"
    }
}
