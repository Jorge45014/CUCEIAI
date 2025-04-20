package com.example.cuceiai

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class iniciar_sesion : AppCompatActivity() {

    private lateinit var textView13: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)

        textView13 = findViewById(R.id.textView13)
        textView13.setOnClickListener {
            val intent = Intent(this,Recuperarcuenta::class.java)
            startActivity(intent)
        }

        // TODO: Implementar la inicialización de la actividad aquí
    }

    override fun onSupportNavigateUp(): Boolean {
        // TODO: Implementar la navegación hacia arriba aquí
        return super.onSupportNavigateUp()
    }
}