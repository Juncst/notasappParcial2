package com.example.notasapp.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Esta IP comunica el emulador Android con el backend de Windows.
    private const val BASE_URL = "http://10.0.2.2:3000/"

    fun crearApi(context: Context): ApiService {
        val preferencias = context.applicationContext.getSharedPreferences(
            "sesion", Context.MODE_PRIVATE
        )

        val cliente = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { cadena ->
                val solicitud = cadena.request()
                val nuevaSolicitud = solicitud.newBuilder()

                if (!solicitud.url.encodedPath.endsWith("/api/auth/login")) {
                    val token = preferencias.getString("token", null)
                    if (!token.isNullOrEmpty()) {
                        nuevaSolicitud.header("Authorization", "Bearer $token")
                    }
                }
                val respuesta = cadena.proceed(nuevaSolicitud.build())
                if (solicitud.url.encodedPath != "/api/auth/login" &&
                    (respuesta.code == 401 || respuesta.code == 403)) {
                    preferencias.edit().clear().apply()
                }
                respuesta
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
