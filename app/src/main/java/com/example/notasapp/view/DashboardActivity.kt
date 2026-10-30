package com.example.notasapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.notasapp.MainActivity
import com.example.notasapp.R

class DashboardActivity : ActivityConSesion() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!comprobarSesion()) return
        setContentView(R.layout.activity_dashboard)

        val preferencias = getSharedPreferences("sesion", MODE_PRIVATE)
        val nombre = preferencias.getString("nombre", "Usuario")
        findViewById<TextView>(R.id.txtBienvenida).text = "Bienvenido, $nombre"

        findViewById<Button>(R.id.btnCrearNota).setOnClickListener {
            startActivity(Intent(this, CrearNotaActivity::class.java))
        }
        findViewById<Button>(R.id.btnConsultarNotas).setOnClickListener {
            startActivity(Intent(this, ListadoNotasActivity::class.java))
        }
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            preferencias.edit().clear().apply()
            comprobarSesion()
        }
    }
}
