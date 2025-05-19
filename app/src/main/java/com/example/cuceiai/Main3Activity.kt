package com.example.cuceiai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.cuceiai.databinding.ActivityMain3Binding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cuceiai.ProductoPrecio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CountDownLatch
import kotlin.jvm.java

enum class ProviderType{
    BASIC,
    GOOGLE
}
data class ProductoPrecio(
    val producto: String,
    val precio: Double
)

class Main3Activity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain3Binding
    private lateinit var buttonMenu: ImageButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResultadoAdapter
    private lateinit var listaCompleta: List<Productos>
    private lateinit var listaCompleta2: List<Productos>
    private lateinit var tituloContenedor: TextView

    private var botonActivo: Int = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = findViewById(R.id.ProductosR)

        tituloContenedor = findViewById(R.id.textView8e)


        setSupportActionBar(binding.appBarMain3.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main3)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        buttonMenu = findViewById(R.id.buttonMenu)
        buttonMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_slideshow -> {
                    val intent = Intent(this, SoporteActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_acerca_de -> {
                    val intent = Intent(this, activity_acercade::class.java)
                    startActivity(intent)
                    true
                }
                R.id.cerrar_sesion -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            botonActivo = item.itemId
            when (item.itemId) {
                R.id.nav_home -> {
                    tituloContenedor.text = "Precios de este mes"
                    verificarBotonActivo()
                    if (this::class.java != Main3Activity::class.java) {
                        val intent = Intent(this, Main3Activity::class.java)
                        finish()
                        startActivity(intent)
                    }
                    true
                }
                R.id.statistics -> {
                    tituloContenedor.text = "Estimar el siguiente mes"
                    verificarBotonActivo()
                    true
                }
                /*
                R.id.nav_history -> {
                    true
                }
                R.id.nav_profile -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }*/
                else -> false
            }
        }

        //setup
        setup()
        configurarBuscador()

        // Barra de búsqueda
        val buscador = findViewById<EditText>(R.id.autoCompleteTextView)
        recyclerView = findViewById(R.id.ProductosR)

        // Lista simulada (aquí pondrías los datos de tu base de datos) /////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Tu lista de productos
        val productos = listOf("Aceite Mixto", "Aguacate Hass", "Arroz largo", "Azúcar Estándar", "Bistec Diezmillo de Res", "Calabacita Italiana", "Carne Molida", "Carne Molida Sirloin 90-10", "Cebolla bola", "Chayote sin espina", "Chile poblano", "Chile serrano", "Frijol Flor de Mayo", "Frijol Negro", "Guayaba", "Harina de Trigo", "Huevo Blanco", "Jitomate Saladette", "Lechuga romana", "Limón con semilla", "Manzana Golden", "Manzana Starking", "Naranja mediana", "Papa alpha", "Papaya maradol", "Pepino", "Piña", "Plátano", "Sandia", "Sandía", "Sirloin 90-10", "Tomate verde", "Zanahoria mediana")

        val db = FirebaseFirestore.getInstance()

        //val meses = listOf( "nov24", "dic24","ene25", "feb25", "mar25", "abr25")
        val meses = listOf(
             "jun24",
            "jul24", "ago24", "sep24", "oct24", "nov24", "dic24",
            "ene25", "feb25", "mar25", "abr25", "may25", "jun25"
        )
//        val meses = listOf(
//            "ene24", "feb24", "mar24", "abr24", "may24", "jun24",
//            "jul24", "ago24", "sep24", "oct24", "nov24", "dic24",
//            "ene25", "feb25", "mar25", "abr25", "may25", "jun25"
//        )

        val listaProximoMes = mutableListOf<com.example.cuceiai.ProductoPrecio>()

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

                // Mostrar lista ordenada en consola
                println("Lista próxima mes ordenada:")
                for (item in listaOrdenada) {
                    println("${item.producto}: ${"%.2f".format(item.precio)}")
                }

                // LOG para verificar antes de convertir
                Log.d("Depuracion", "listaOrdenada contiene ${listaOrdenada.size} elementos")
                listaOrdenada.forEach {
                    Log.d("Depuracion", "ProductoPrecio -> ${it.producto}: ${it.precio}")
                }

                // Convertir a objetos Productos
                val nuevaListaProductos = listaOrdenada.map {
                    Productos(nombre = it.producto, precio = it.precio)
                }

                // LOG para verificar la nueva lista
                Log.d("Depuracion", "nuevaListaProductos contiene ${nuevaListaProductos.size} elementos")
                nuevaListaProductos.forEach {
                    Log.d("Depuracion", "Productos -> ${it.nombre}: ${it.precio}")
                }

                // Asignar a listaCompleta2
                listaCompleta2 = nuevaListaProductos
            }
        }.start()

        val ultimoMes = "abr25"
        val listaTemporal = mutableListOf<Productos>()

        for (nombreProducto in productos) {
            db.collection("productos")
                .document(nombreProducto)
                .collection(ultimoMes)
                .document("datos")
                .get()
                .addOnSuccessListener { documento ->
                    if (documento.exists()) {
                        val datosPrecio = documento.toObject(PreciosMensuales::class.java)
                        if (datosPrecio != null) {
                            val nuevoProducto = Productos(
                                nombre = nombreProducto,
                                precio = datosPrecio.tiendap
                            )

                            synchronized(listaTemporal) {
                                listaTemporal.add(nuevoProducto)
                            }

                            // (Opcional) Actualizar UI conforme se reciban los datos
                            runOnUiThread {
                                val listaOrdenada = listaTemporal.sortedBy { it.nombre }

                                // LOG o impresión en consola
                                println("Actualización parcial:")
                                listaOrdenada.forEach {
                                    println("${it.nombre}: ${"%.2f".format(it.precio)}")
                                }

                                // Asignar a la lista completa
                                listaCompleta = listaOrdenada
                            }
                        }
                    }
                }
        }





        listaCompleta = listOf(
            Productos("Aceite Mixto", 16.98),
            Productos("Aguacate Hass", 33.38),
            Productos("Arroz largo", 11.25),
            Productos("Azúcar Estándar", 14.48),
            Productos("Bistec Diesmillo de Res", 113.65),
            Productos("Calabacita Italiana", 18.92),
            Productos("Carne Molida", 113.65),
            Productos("Cebolla Bola", 14.58),
            Productos("Chayote sin espina", 12.45)
        )

//        listaCompleta2 = listOf(
//            Productos("Arroz", 28.50),
//            Productos("Aceite vegetal", 42.90),
//            Productos("Leche entera", 23.75),
//            Productos("Huevos (docena)", 36.10),
//            Productos("Pan de caja", 29.40),
//            Productos("Arroz", 28.50),
//            Productos("Aceite vegetal", 42.90),
//            Productos("Leche entera", 23.75),
//            Productos("Huevos (docena)", 36.10),
//            Productos("Pan de caja", 29.40)
//        )

        verificarBotonActivo()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buscador.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().trim()
                val resultadosFiltrados = listaCompleta.filter {
                    it.nombre.contains(texto, ignoreCase = true) ||
                            it.precio.toString().contains(texto)
                }

                if (texto.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.GONE
                }

                adapter.actualizarLista(resultadosFiltrados, botonActivo)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun verificarBotonActivo() {
        when (botonActivo) {
            R.id.nav_home -> actualizarAdapter(listaCompleta, botonActivo) // Cambiar aquí
            R.id.statistics -> actualizarAdapter(listaCompleta2, botonActivo) // Cambiar aquí
        }
        recyclerView.adapter = adapter
    }

    private fun actualizarAdapter(lista: List<Productos>, botonId: Int) { // Cambiar aquí
        if (::adapter.isInitialized) {
            adapter.actualizarLista(lista, botonId) // Pasar ambos parámetros
        } else {
            adapter = ResultadoAdapter(lista, botonId) // Inicializar con el botón
        }
        recyclerView.visibility = View.VISIBLE
    }

    ///////////////////////////////////////////////////////////////////////////////////ds/////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main3, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main3)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setup(){
        title = "Inicio"
    }

    private fun configurarBuscador() {
        val buscador = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val textView8 = findViewById<TextView>(R.id.textView8e)
        val textView9 = findViewById<TextView>(R.id.textView9)

        // Este bloque se ejecutará cuando empiecen a escribir
        buscador.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString()

                // Si hay texto, ocultar las vistas
                if (texto.isNotEmpty()) {
                    textView8.visibility = View.GONE
                    textView9.visibility = View.GONE
                } else {
                    // Si no hay texto, mostrar las vistas
                    textView8.visibility = View.VISIBLE
                    textView9.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // También manejamos el evento de búsqueda con el botón
        buscador.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {

                val texto = buscador.text.toString().trim()

                if (texto.isNotEmpty()) {
                    realizarBusqueda(texto)

                    // Ocultar vistas después de buscar
                    textView8.visibility = View.GONE
                    textView9.visibility = View.GONE
                } else {
                    // Mostrar vistas si no hay texto
                    textView8.visibility = View.VISIBLE
                    textView9.visibility = View.VISIBLE
                }

                true
            } else {
                false
            }
        }
    }

    private fun realizarBusqueda(texto: String) {
        Toast.makeText(this, "Buscando: $texto", Toast.LENGTH_SHORT).show()
    }


}