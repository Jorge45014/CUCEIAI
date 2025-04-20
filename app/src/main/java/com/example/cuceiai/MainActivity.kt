package com.example.cuceiai

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.TextView
import androidx.lifecycle.findViewTreeViewModelStoreOwner


class MainActivity : AppCompatActivity() {
    private lateinit var button_ini_sesion_principal: Button
    private lateinit var Registrarse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        button_ini_sesion_principal = findViewById(R.id.button_ini_sesion_principal)
        button_ini_sesion_principal.setOnClickListener {
            val intent = Intent(this,iniciar_sesion::class.java)
            startActivity(intent)
        }
        Registrarse = findViewById(R.id.textView4)
        Registrarse.setOnClickListener {
            val intent = Intent(this,crear_cuenta::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}