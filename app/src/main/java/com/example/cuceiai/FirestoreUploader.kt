package com.example.cuceiai

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.round


data class PreciosMensuales(
    var tiendap: Double = 0.0,
    var mercadoRuedasp: Double = 0.0,
    var mercadosPublicosp: Double = 0.0
)


fun guardarProductoFirestore(
    nombreProducto: String,
    mes: String,
    precioTienda: Pair<Double, Double>,
    precioMercadoR: Pair<Double, Double>,
    precioMercadoP: Pair<Double, Double>
) {
    val db = FirebaseFirestore.getInstance()

    val promedio = { r: Pair<Double, Double> -> (r.first + r.second) / 2.0 / 2.0 }

    val datos = PreciosMensuales(
        tiendap = round(promedio(precioTienda) * 100) / 100,
        mercadoRuedasp = round(promedio(precioMercadoR) * 100) / 100,
        mercadosPublicosp = round(promedio(precioMercadoP) * 100) / 100
    )

    db.collection("productos")
        .document(nombreProducto)
        .collection(mes)
        .document("datos")
        .set(datos)
        .addOnSuccessListener {
            println("Datos guardados correctamente para $nombreProducto en $mes")
        }
        .addOnFailureListener { e ->
            println("Error al guardar: ${e.message}")
        }
}
