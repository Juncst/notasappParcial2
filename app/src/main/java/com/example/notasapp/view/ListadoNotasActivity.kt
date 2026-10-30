package com.example.notasapp.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notasapp.R
import com.example.notasapp.network.RetrofitClient
import com.example.notasapp.repository.NotasRepository
import com.example.notasapp.viewmodel.EstadoListado
import com.example.notasapp.viewmodel.NotasViewModel
import com.example.notasapp.viewmodel.NotasViewModelFactory

class ListadoNotasActivity : ActivityConSesion() {
    private lateinit var viewModel: NotasViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!comprobarSesion()) return
        setContentView(R.layout.activity_listado_notas)

        val listado = findViewById<RecyclerView>(R.id.recyclerNotas)
        val progreso = findViewById<ProgressBar>(R.id.progresoListado)
        val mensajeVacio = findViewById<TextView>(R.id.txtSinNotas)
        val adapter = NotasAdapter()
        listado.layoutManager = LinearLayoutManager(this)
        listado.adapter = adapter

        val factory = NotasViewModelFactory(NotasRepository(RetrofitClient.crearApi(this)))
        viewModel = ViewModelProvider(this, factory)[NotasViewModel::class.java]

        findViewById<Button>(R.id.btnVolverListado).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnReintentar).setOnClickListener { viewModel.consultarNotas() }

        viewModel.estadoListado.observe(this) { estado ->
            when (estado) {
                is EstadoListado.Cargando -> {
                    findViewById<Button>(R.id.btnReintentar).visibility = View.GONE
                    progreso.visibility = View.VISIBLE
                    mensajeVacio.visibility = View.GONE
                }
                is EstadoListado.Resultado -> {
                    mensajeVacio.text = "Todavía no tienes notas guardadas"
                    progreso.visibility = View.GONE
                    adapter.actualizar(estado.notas)
                    mensajeVacio.visibility = if (estado.notas.isEmpty()) View.VISIBLE else View.GONE
                }
                is EstadoListado.Error -> {
                    findViewById<Button>(R.id.btnReintentar).visibility = View.VISIBLE
                    progreso.visibility = View.GONE
                    mensajeVacio.visibility = View.VISIBLE
                    mensajeVacio.text = "No fue posible cargar las notas"
                    Toast.makeText(this, estado.mensaje, Toast.LENGTH_LONG).show()
                    comprobarSesion()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isFinishing && ::viewModel.isInitialized) viewModel.consultarNotas()
    }
}
