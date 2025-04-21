package com.example.cuceiai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent



class crear_cuenta : AppCompatActivity() {

    // Declarar variables para los EditText y el botón
    private lateinit var nombre: EditText
    private lateinit var correo: EditText
    private lateinit var contrasena: EditText
    private lateinit var botonCrearCuenta: Button
    private var db =FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_cuenta)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Enlazar vistas
        nombre = findViewById(R.id.textView14)
        correo = findViewById(R.id.editTextTextEmailAddress3)
        contrasena = findViewById(R.id.editTextTextEmailAddress7)
        botonCrearCuenta = findViewById(R.id.button2)

        setup()
    }

    private fun setup() {
        title = "Crear cuenta"

        botonCrearCuenta.setOnClickListener {

            val nombreTexto = nombre.text.toString().trim()
            val correoTexto = correo.text.toString().trim()
            val contrasenaTexto = contrasena.text.toString().trim()

            if (nombreTexto.isNotEmpty() && correoTexto.isNotEmpty() && contrasenaTexto.isNotEmpty()) {

                val datosUsuario = hashMapOf(
                    "nombre" to nombreTexto,
                    "correo" to correoTexto,
                    "contrasena" to contrasenaTexto
                )

                db.collection("users").document(correoTexto).set(datosUsuario)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()

                        // Si quieres cambiar a otra pantalla después del registro:
                        val intent = Intent(this, iniciar_sesion::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                    }

            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}