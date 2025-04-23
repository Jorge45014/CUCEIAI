package com.example.cuceiai

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class iniciar_sesion : AppCompatActivity() {

    private lateinit var textoOlvidaste: TextView
    private lateinit var textoRegistrate: TextView
    private lateinit var botonSesion: Button
    private lateinit var correoLogin: EditText
    private lateinit var contrasenaLogin: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        // Referencias de vistas
        textoOlvidaste = findViewById(R.id.ovidastecontra)
        textoRegistrate = findViewById(R.id.registrateinic)
        botonSesion = findViewById(R.id.buttonLogin)
        correoLogin = findViewById(R.id.correoLogin)
        contrasenaLogin = findViewById(R.id.contrasenaLogin)

        textoOlvidaste.setOnClickListener {
            val intent = Intent(this, Recuperarcuenta::class.java)
            startActivity(intent)
        }

        textoRegistrate.setOnClickListener {
            val intent = Intent(this, crear_cuenta::class.java)
            startActivity(intent)
        }

        setup()
    }

    private fun setup() {
        title = "Iniciar Sesión"

        botonSesion.setOnClickListener {
            val correo = correoLogin.text.toString()
            val contrasena = contrasenaLogin.text.toString()

            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            irInicio(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            showAlert()
                        }
                    }
            } else {
                if (correo.isEmpty()) correoLogin.error = "Campo requerido"
                if (contrasena.isEmpty()) contrasenaLogin.error = "Campo requerido"
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