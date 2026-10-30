package com.example.notasapp.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.notasapp.R
import com.example.notasapp.network.RetrofitClient
import com.example.notasapp.repository.NotasRepository
import com.example.notasapp.viewmodel.EstadoGuardar
import com.example.notasapp.viewmodel.NotasViewModel
import com.example.notasapp.viewmodel.NotasViewModelFactory

class CrearNotaActivity : ActivityConSesion() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!comprobarSesion()) return
        setContentView(R.layout.activity_crear_nota)

        val titulo = findViewById<EditText>(R.id.edtTituloNota)
        val contenido = findViewById<EditText>(R.id.edtContenidoNota)
        val guardar = findViewById<Button>(R.id.btnGuardarNota)
        val progreso = findViewById<ProgressBar>(R.id.progresoGuardar)

        val factory = NotasViewModelFactory(NotasRepository(RetrofitClient.crearApi(this)))
        val viewModel = ViewModelProvider(this, factory)[NotasViewModel::class.java]

        guardar.setOnClickListener {
            viewModel.guardarNota(titulo.text.toString(), contenido.text.toString())
        }
        findViewById<Button>(R.id.btnVolverCrear).setOnClickListener { finish() }

        viewModel.estadoGuardar.observe(this) { estado ->
            when (estado) {
                is EstadoGuardar.Cargando -> {
                    progreso.visibility = View.VISIBLE
                    guardar.isEnabled = false
                }
                is EstadoGuardar.Guardada -> {
                    progreso.visibility = View.GONE
                    guardar.isEnabled = true
                    Toast.makeText(this, "Nota guardada correctamente", Toast.LENGTH_LONG).show()
                    finish()
                }
                is EstadoGuardar.Error -> {
                    progreso.visibility = View.GONE
                    guardar.isEnabled = true
                    Toast.makeText(this, estado.mensaje, Toast.LENGTH_LONG).show()
                    comprobarSesion()
                }
            }
        }
    }
}
