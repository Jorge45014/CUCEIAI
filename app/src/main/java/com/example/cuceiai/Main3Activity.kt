package com.example.cuceiai

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.cuceiai.databinding.ActivityAcercadeBinding
import com.example.cuceiai.databinding.ActivityMain3Binding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC
}

class Main3Activity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain3Binding
    private lateinit var buttonMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain3.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main3)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
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
                // Aquí puedes manejar otros ítems si es necesario
                else -> false
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this::class.java != Main3Activity::class.java) {
                        val intent = Intent(this, Main3Activity::class.java)
                        finish()
                        startActivity(intent)
                    }
                    true
                }
                R.id.nav_favorites -> {
                    // Acción para Profesores
                    Toast.makeText(this, "Profesores seleccionados", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_history -> {
                    // Acción para Notificaciones
                    Toast.makeText(this, "Notificaciones seleccionadas", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    // Acción para Perfil
                    FirebaseAuth.getInstance().signOut()

                    // Ir a la pantalla de inicio de sesión y limpiar el historial
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


        //setup
        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
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
}