package com.example.cuceiai

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlin.jvm.java

class ResultadoAdapter(private var lista: List<String>) :
    RecyclerView.Adapter<ResultadoAdapter.ResultadoViewHolder>() {

    inner class ResultadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val texto: TextView = itemView.findViewById(R.id.itemTexto)
        val contenedor: View = itemView.findViewById(R.id.ContDeTexto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resultado, parent, false)
        return ResultadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        val item = lista[position]
        holder.texto.text = item

        holder.contenedor.setOnClickListener {
            // Aquí puedes hacer cualquier acción
            // Ejemplo: lanzar una nueva actividad

            val context = holder.itemView.context
            Toast.makeText(context, "¡Elemento presionado!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<String>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}