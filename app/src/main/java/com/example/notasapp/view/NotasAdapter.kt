package com.example.notasapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notasapp.R
import com.example.notasapp.model.Nota

class NotasAdapter : RecyclerView.Adapter<NotasAdapter.NotaViewHolder>() {
    private val notas = mutableListOf<Nota>()

    class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.txtTituloTarjeta)
        val contenido: TextView = itemView.findViewById(R.id.txtContenidoTarjeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = notas[position]
        holder.titulo.text = nota.titulo
        holder.contenido.text = nota.contenido
    }

    override fun getItemCount() = notas.size

    fun actualizar(lista: List<Nota>) {
        notas.clear()
        notas.addAll(lista)
        notifyDataSetChanged()
    }
}
