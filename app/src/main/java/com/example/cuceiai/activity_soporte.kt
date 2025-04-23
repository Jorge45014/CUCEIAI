package com.example.cuceiai

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.cuceiai.databinding.ActivityAcercadeBinding
import com.google.android.material.navigation.NavigationView
import com.example.cuceiai.databinding.ActivitySoporteBinding

class SoporteActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySoporteBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var buttonMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySoporteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        buttonMenu = findViewById(R.id.buttonMenu)
        buttonMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_slideshow -> {
                    val intent = Intent(this,SoporteActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_acerca_de -> {
                    val intent = Intent(this, activity_acercade::class.java)
                    startActivity(intent)
                    true
                }
                // Agrega más navegación aquí si lo necesitas
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main3, menu)
        return true
    }
}