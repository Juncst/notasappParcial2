package com.example.notasapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.notasapp.MainActivity

/** Evita conservar pantallas protegidas cuando caduca o se cierra la sesión. */
abstract class ActivityConSesion : AppCompatActivity() {
    protected fun comprobarSesion(): Boolean {
        if (!getSharedPreferences("sesion", MODE_PRIVATE).getString("token", null).isNullOrBlank()) {
            return true
        }
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
        return false
    }

    override fun onResume() {
        super.onResume()
        comprobarSesion()
    }
}
