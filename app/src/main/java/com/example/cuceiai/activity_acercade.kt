package com.example.cuceiai

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cuceiai.databinding.ActivityAcercadeBinding
import com.google.android.material.navigation.NavigationView

class activity_acercade : AppCompatActivity() {

    private lateinit var binding: ActivityAcercadeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var buttonMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAcercadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.appBarMain3.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main3)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_acerca_de
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        buttonMenu = findViewById(R.id.buttonMenu)
        buttonMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_slideshow -> {
                    startActivity(Intent(this, SoporteActivity::class.java))
                    true
                }
                R.id.nav_acerca_de -> {
                    // Ya estás en esta activity, opcionalmente puedes recargar
                    val intent = Intent(this, activity_acercade::class.java)
                    finish() // Finaliza la actividad actual
                    startActivity(intent) // Inicia una nueva instancia

                    true
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, Main3Activity::class.java))
                    true
                }
                // Puedes agregar más botones aquí
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main3, menu)
        return true
    }
}

