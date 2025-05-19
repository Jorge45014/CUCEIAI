package com.example.cuceiai

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import java.util.concurrent.CountDownLatch
import kotlin.text.toDouble

data class ProductoPrecio(
    val producto: String,
    val precio: Double
)

class MainActivity : AppCompatActivity() {
    private lateinit var button_ini_sesion_principal: Button
    private lateinit var button_ini_sesion_google: Button
    private lateinit var Accesomn : Button
    private val db = FirebaseFirestore.getInstance()
    private var GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            startActivity(Intent(this, Main3Activity::class.java))
        } else {
            // No hay usuario autenticado
            button_ini_sesion_principal = findViewById(R.id.button_ini_sesion_principal)
            button_ini_sesion_principal.setOnClickListener {
                val intent = Intent(this,iniciar_sesion::class.java)
                startActivity(intent)
            }


            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            Accesomn = findViewById(R.id.Acesomn)
            Accesomn.setOnClickListener {
                val intent = Intent(this, Main3Activity::class.java)
                startActivity(intent)
            }

            setup()




        }

        // Tu lista de productos
        val productos = listOf("Aceite Mixto", "Aguacate Hass", "Arroz largo", "Azúcar Estándar", "Bistec Diezmillo de Res", "Calabacita Italiana", "Carne Molida", "Carne Molida Sirloin 90-10", "Cebolla bola", "Chayote sin espina", "Chile poblano", "Chile serrano", "Frijol Flor de Mayo", "Frijol Negro", "Guayaba", "Harina de Trigo", "Huevo Blanco", "Jitomate Saladette", "Lechuga romana", "Limón con semilla", "Manzana Golden", "Manzana Starking", "Naranja mediana", "Papa alpha", "Papaya maradol", "Pepino", "Piña", "Plátano", "Sandia", "Sandía", "Sirloin 90-10", "Tomate verde", "Zanahoria mediana")

        val db = FirebaseFirestore.getInstance()
        val meses = listOf( "nov24", "dic24","ene25", "feb25", "mar25", "abr25")

        val listaProximoMes = mutableListOf<ProductoPrecio>()

        // Un CountDownLatch para esperar a que terminen todas las consultas (productos * meses)
        val latchProductos = CountDownLatch(productos.size)

        for (producto in productos) {
            val datosPorMes = mutableMapOf<String, Double>()
            val latchMeses = CountDownLatch(meses.size)

            for (mes in meses) {
                db.collection("productos")
                    .document(producto)
                    .collection(mes)
                    .document("datos")
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val precios = document.toObject(PreciosMensuales::class.java)
                            if (precios != null) {
                                datosPorMes[mes] = precios.tiendap
                            }
                        }
                        latchMeses.countDown()
                    }
                    .addOnFailureListener {
                        latchMeses.countDown()
                    }
            }

            // Esperar que terminen los meses para este producto
            Thread {
                latchMeses.await()

                val listaTiendapOrdenada = meses.mapNotNull { mes ->
                    datosPorMes[mes]?.let { valor -> Pair(mes, valor) }
                }

                if (listaTiendapOrdenada.isNotEmpty()) {
                    val listaDoubles = listaTiendapOrdenada.map { it.second }

                    // Aquí pones tu regresión polinomial
                    val bestDegree = 2 // Por simplicidad, fijo 2
                    val modelFinal = PolynomialRegression(bestDegree)
                    modelFinal.fit(listaDoubles)
                    val nextX = listaDoubles.size + 1.0
                    val prediction = modelFinal.predict(nextX)
                    println("lista tienda $listaDoubles")

                    synchronized(listaProximoMes) {
                        listaProximoMes.add(ProductoPrecio(producto, prediction))
                    }
                }
                latchProductos.countDown()
            }.start()
        }

        // Esperar a que terminen todos los productos
        Thread {
            latchProductos.await()
            runOnUiThread {
                // Ordenar alfabéticamente por nombre del producto
                val listaOrdenada = listaProximoMes.sortedBy { it.producto }

                // Mostrar lista ordenada
                println("Lista próxima mes ordenada:")
                for (item in listaOrdenada) {
                    println("${item.producto}: ${"%.2f".format(item.precio)}")
                }
            }
        }.start()




    }

    // Función para calcular error cuadrático medio (MSE)
    fun meanSquaredError(yTrue: List<Double>, yPred: List<Double>): Double {
        var sum = 0.0
        for (i in yTrue.indices) {
            sum += (yTrue[i] - yPred[i]) * (yTrue[i] - yPred[i])
        }
        return sum / yTrue.size
    }




    private fun setup(){
        button_ini_sesion_google = findViewById(R.id.buttonInicgoogle)
        button_ini_sesion_google.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleCliete = GoogleSignIn.getClient(this, googleConf)
            googleCliete.signOut()

            startActivityForResult(googleCliete.signInIntent, GOOGLE_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val acount = task.getResult(ApiException::class.java)
                if (acount != null) {
                    val credencial = GoogleAuthProvider.getCredential(acount.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credencial)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                irInicio(acount.email ?:"", ProviderType.GOOGLE)
                                val nombreTexto = acount.displayName ?: "Nombre no disponible"
                                val correoTexto = acount.email ?: "Correo no disponible"
                                val datosUsuario = hashMapOf(
                                    "nombre" to nombreTexto,
                                    "correo" to correoTexto
                                    // No se guarda la contraseña por seguridad
                                )

                                db.collection("users").document(correoTexto).set(datosUsuario)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                showAlert()
                            }
                        }
                }

            }catch (e: ApiException){
                showAlert()
            }


        }

    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        builder.create().show()
    }

    private fun irInicio(correo: String, proveedor: ProviderType) {
        val homeIntent = Intent(this, Main3Activity::class.java).apply {
            putExtra("correo", correo)
            putExtra("proveedor", proveedor.name)
        }
        startActivity(homeIntent)
    }






}
