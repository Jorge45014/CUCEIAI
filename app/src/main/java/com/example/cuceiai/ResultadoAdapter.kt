package com.example.cuceiai

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlin.jvm.java
data class Profesor(
    val id: String = "",
    val nombre: String = "",
    val especialidad: String = "",
    val rating_promedio: Double = 0.0

)

class ResultadoAdapter(private var lista: List<Profesor>) :
    RecyclerView.Adapter<ResultadoAdapter.ResultadoViewHolder>() {

    inner class ResultadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val texto: TextView = itemView.findViewById(R.id.itemTexto) // Nombre del profesor
        val textoDes: TextView = itemView.findViewById(R.id.textoDescripcion) // Calificación
        val contenedor: View = itemView.findViewById(R.id.ContDeTexto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resultado, parent, false)
        return ResultadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        val profesor = lista[position]
        holder.texto.text = profesor.nombre
        holder.textoDes.text = "Calificación: %.2f".format(profesor.rating_promedio)

        holder.contenedor.setOnClickListener {
            val context = holder.itemView.context
            Toast.makeText(context, "Profesor: ${profesor.nombre}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Profesor>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}