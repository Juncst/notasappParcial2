package com.example.notasapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.notasapp.network.RetrofitClient
import com.example.notasapp.repository.AuthRepository
import com.example.notasapp.view.DashboardActivity
import com.example.notasapp.viewmodel.EstadoLogin
import com.example.notasapp.viewmodel.LoginViewModel
import com.example.notasapp.viewmodel.LoginViewModelFactory

class MainActivity : AppCompatActivity() {
    private val permisoRed = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permitido ->
        if (permitido) {
            iniciarSesion()
        } else {
            Toast.makeText(this, "Permite el acceso a la red local para conectar con el servidor. Puedes habilitarlo en Ajustes > Aplicaciones > NotasApp > Permisos.", Toast.LENGTH_LONG).show()
        }
    }

    private lateinit var viewModel: LoginViewModel

    private fun iniciarSesion() {
        viewModel.iniciarSesion(
            findViewById<EditText>(R.id.edtEmail).text.toString(),
            findViewById<EditText>(R.id.edtPassword).text.toString()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val boton = findViewById<Button>(R.id.btnLogin)
        val progreso = findViewById<ProgressBar>(R.id.progresoLogin)

        val api = RetrofitClient.crearApi(this)
        val factory = LoginViewModelFactory(AuthRepository(api))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        boton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 37 && ContextCompat.checkSelfPermission(
                    this, "android.permission.ACCESS_LOCAL_NETWORK"
                ) != PackageManager.PERMISSION_GRANTED) {
                permisoRed.launch("android.permission.ACCESS_LOCAL_NETWORK")
            } else {
                iniciarSesion()
            }
        }

        viewModel.estado.observe(this) { estado ->
            when (estado) {
                is EstadoLogin.Cargando -> {
                    progreso.visibility = View.VISIBLE
                    boton.isEnabled = false
                }
                is EstadoLogin.Correcto -> {
                    progreso.visibility = View.GONE
                    boton.isEnabled = true
                    getSharedPreferences("sesion", MODE_PRIVATE).edit()
                        .putString("token", estado.token)
                        .putString("nombre", estado.usuario.nombre)
                        .apply()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
                is EstadoLogin.Error -> {
                    progreso.visibility = View.GONE
                    boton.isEnabled = true
                    Toast.makeText(this, estado.mensaje, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
