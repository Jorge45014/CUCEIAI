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
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.jvm.java

enum class ProviderType{
    BASIC,
    GOOGLE
}

class Main3Activity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain3Binding
    private lateinit var buttonMenu: ImageButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResultadoAdapter
    private lateinit var listaCompleta: List<Profesor>
    private lateinit var listaCompleta2: List<Profesor>
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
                else -> false
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            botonActivo = item.itemId
            when (item.itemId) {
                R.id.nav_home -> {
                    tituloContenedor.text = "Este Mes"
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

        listaCompleta = listOf(
            Profesor("1", "Ana López", "Matemáticas", 4.8),
            Profesor("2", "Luis Gómez", "Física", 4.5),
            Profesor("3", "María Pérez", "Historia", 4.9),
            Profesor("4", "Carlos Torres", "Química", 4.2),
            Profesor("5", "Sofía Ramírez", "Biología", 4.6)
        )

        listaCompleta2 = listOf(
            Profesor("101", "Arroz", "Grano", 28.50),
            Profesor("102", "Aceite vegetal", "Aceites", 42.90),
            Profesor("103", "Leche entera", "Lácteos", 23.75),
            Profesor("104", "Huevos (docena)", "Proteína", 36.10),
            Profesor("105", "Pan de caja", "Panadería", 29.40),
            Profesor("101", "Arroz", "Grano", 28.50),
            Profesor("102", "Aceite vegetal", "Aceites", 42.90),
            Profesor("103", "Leche entera", "Lácteos", 23.75),
            Profesor("104", "Huevos (docena)", "Proteína", 36.10),
            Profesor("105", "Pan de caja", "Panadería", 29.40)
        )


        verificarBotonActivo()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buscador.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().trim()
                val resultadosFiltrados = listaCompleta.filter {
                    it.nombre.contains(texto, ignoreCase = true) ||
                            it.especialidad.contains(texto, ignoreCase = true)
                }

                if (texto.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.GONE
                }

                adapter.actualizarLista(resultadosFiltrados)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun verificarBotonActivo() {
        when (botonActivo) {
            R.id.nav_home -> actualizarAdapter(listaCompleta)
            R.id.statistics -> actualizarAdapter(listaCompleta2)
        }
        recyclerView.adapter = adapter
    }

    private fun actualizarAdapter(lista: List<Profesor>) {
        if (::adapter.isInitialized) {
            adapter.actualizarLista(lista)
        } else {
            adapter = ResultadoAdapter(lista)
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