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
        Pair("jun24", 14.48),
        Pair("jul24", 17.4),
        Pair("ago24", 15.25),
        Pair("sep24", 15.98),
        Pair("oct24", 14.7),
        Pair("nob24", 15.00),
        Pair("dic24", 14.48),
        Pair("ene25", 14.48),
        Pair("feb25", 16.2),
        Pair("mar25", 16.7),
        Pair("abr25", 17.5),
        Pair("may25", 16.98),
        Pair("jun25", 19.05)
    )


    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        val producto = lista[position]
        holder.texto.text = producto.nombre
        holder.textoDes.text = "Precio: %.2f $ - 1 kg".format(producto.precio)

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
                        color = Color.BLACK
                        setValueTextColor(Color.BLACK) // Color por defecto
                        lineWidth = 2f

                        // Configurar colores de los puntos
                        setCircleColors(
                            List(entries.size) { i ->
                                if (i == entries.lastIndex) Color.RED else Color.BLACK
                            }
                        )

                        // Configurar colores del texto de los valores
                        val valueColors = entries.mapIndexed { index, _ ->
                            if (index == entries.lastIndex) Color.RED else Color.BLACK
                        }

                        setValueTextColors(valueColors.map { listOf(it) }) // Formato requerido

                        circleRadius = 4f
                        circleHoleRadius = 2f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return "%.2f".format(value)
                            }
                        }
                    }

                    dataSet.color = if (entries.size > 1) {
                        Color.rgb(0, 0, 0)
                    } else {
                        Color.RED
                    }

                    val lineData = LineData(dataSet)
                    lineChart.apply {
                        data = lineData
                        description.isEnabled = false
                        legend.textColor = Color.BLACK
                        xAxis.textColor = Color.BLACK
                        axisLeft.textColor = Color.BLACK
                        description.textColor = Color.BLACK
                        xAxis.apply {
                            textColor = Color.BLACK
                            valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return datos.getOrNull(value.toInt())?.first ?: ""
                                }
                            }
                        }
                        axisRight.isEnabled = false
                        invalidate()
                    }

                    tvInfo.text = "${producto.nombre}\nPrecio actual: ${"%.2f".format(producto.precio)}"

                    dialog.show()

                    val width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
                    val height = (context.resources.displayMetrics.heightPixels * 0.64).toInt()
                    dialog.window?.setLayout(width, height)
                }
                else -> {
                    Toast.makeText(context, "Producto: ${producto.nombre}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setValueTextColors(lists: List<List<Int>>) {}

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Productos>, nuevoBotonActivo: Int) {
        lista = nuevaLista
        botonActivo = nuevoBotonActivo
        notifyDataSetChanged()
    }
}