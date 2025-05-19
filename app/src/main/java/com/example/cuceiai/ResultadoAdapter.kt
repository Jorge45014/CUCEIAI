package com.example.cuceiai

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Insets.add
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.data.Entry
import kotlin.jvm.java
data class Productos(
    val nombre: String = "",
    val precio: Double = 0.0,
)

class ResultadoAdapter(private var lista: List<Productos>, var botonActivo: Int) :
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

    val datos = listOf(
        Pair("ene", 18.50),
        Pair("feb", 18.75),
        Pair("mar", 19.10),
        Pair("abr", 19.00),
        Pair("may", 19.30),
        Pair("jun", 19.85),
        Pair("jul", 20.10),
        Pair("ago", 20.25),
        Pair("sep", 20.40),
        Pair("oct", 20.60),
        Pair("nob", 21.00),
        Pair("dic", 21.50)
    )


    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        val producto = lista[position]
        holder.texto.text = producto.nombre
        holder.textoDes.text = "Precio: %.2f".format(producto.precio)

        holder.contenedor.setOnClickListener {
            val context = holder.itemView.context
            when (botonActivo) {
                R.id.statistics -> {
                    // Inflar el layout personalizado con la gráfica
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_chart, null)

                    val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
                        .setTitle("Historial de Precios")
                        .setView(dialogView)
                        .setPositiveButton("Aceptar") { d, _ -> d.dismiss() }
                        .create()

                    val lineChart = dialogView.findViewById<LineChart>(R.id.lineChart)
                    val tvInfo = dialogView.findViewById<TextView>(R.id.tvInfo)

                    // Configurar los datos de la gráfica
                    val entries = ArrayList<Entry>().apply {
                        datos.forEachIndexed { index, pair ->
                            add(Entry(index.toFloat(), pair.second.toFloat()))
                        }
                    }

                    val dataSet = LineDataSet(entries, "Precio mensual").apply {
                        color = Color.WHITE
                        valueTextColor = Color.WHITE
                        lineWidth = 2f
                        setCircleColor(Color.WHITE)
                        circleRadius = 4f
                    }

                    val lineData = LineData(dataSet)
                    lineChart.apply {
                        data = lineData
                        description.isEnabled = false
                        legend.textColor = Color.WHITE
                        xAxis.apply {
                            textColor = Color.WHITE
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return datos.getOrNull(value.toInt())?.first ?: ""
                                }
                            }
                        }
                        axisLeft.textColor = Color.WHITE
                        axisRight.isEnabled = false
                        invalidate()
                    }

                    tvInfo.text = "${producto.nombre}\nPrecio actual: ${"%.2f".format(producto.precio)}"

                    dialog.show()

                    val width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
                    val height = (context.resources.displayMetrics.heightPixels * 0.6).toInt()
                    dialog.window?.setLayout(width, height)
                }
                else -> {
                    Toast.makeText(context, "Producto: ${producto.nombre}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Productos>, nuevoBotonActivo: Int) {
        lista = nuevaLista
        botonActivo = nuevoBotonActivo
        notifyDataSetChanged()
    }
}