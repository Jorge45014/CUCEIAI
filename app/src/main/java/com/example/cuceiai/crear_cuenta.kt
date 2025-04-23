package com.example.cuceiai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth


class crear_cuenta : AppCompatActivity() {

    private lateinit var nombre: EditText
    private lateinit var correo: EditText
    private lateinit var contrasena: EditText
    private lateinit var botonCrearCuenta: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_crear_cuenta)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Enlazar vistas con IDs descriptivos
        nombre = findViewById(R.id.textView14)
        correo = findViewById(R.id.contrasenaLogin)
        contrasena = findViewById(R.id.editTextTextEmailAddress7)
        botonCrearCuenta = findViewById(R.id.buttonCrear)

        setup()
    }

    private fun setup() {
        title = "Crear cuenta"

        botonCrearCuenta.setOnClickListener {
            val nombreTexto = nombre.text.toString().trim()
            val correoTexto = correo.text.toString().trim()
            val contrasenaTexto = contrasena.text.toString().trim()

            if (nombreTexto.isNotEmpty() && correoTexto.isNotEmpty() && contrasenaTexto.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(correoTexto, contrasenaTexto)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val datosUsuario = hashMapOf(
                                "nombre" to nombreTexto,
                                "correo" to correoTexto
                                // No se guarda la contraseña por seguridad
                            )

                            db.collection("users").document(correoTexto).set(datosUsuario)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()

                                    // Iniciar sesión automáticamente
                                    FirebaseAuth.getInstance()
                                        .signInWithEmailAndPassword(correoTexto, contrasenaTexto)
                                        .addOnSuccessListener { authResult ->
                                            val email = authResult.user?.email ?: ""
                                            irInicio(email, ProviderType.BASIC)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_LONG).show()
                                        }

                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                                }

                        } else {
                            showAlert()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
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