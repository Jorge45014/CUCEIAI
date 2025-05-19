package com.example.cuceiai

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import java.util.concurrent.CountDownLatch
import kotlin.text.toDouble


class MainActivity : AppCompatActivity() {
    private lateinit var button_ini_sesion_principal: Button
    private lateinit var button_ini_sesion_google: Button
    private lateinit var Accesomn : Button
    private val db = FirebaseFirestore.getInstance()
    private var GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            startActivity(Intent(this, Main3Activity::class.java))
        } else {
            // No hay usuario autenticado
            button_ini_sesion_principal = findViewById(R.id.button_ini_sesion_principal)
            button_ini_sesion_principal.setOnClickListener {
                val intent = Intent(this,iniciar_sesion::class.java)
                startActivity(intent)
            }


            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            Accesomn = findViewById(R.id.Acesomn)
            Accesomn.setOnClickListener {
                val intent = Intent(this, Main3Activity::class.java)
                startActivity(intent)
            }

            setup()




        }






    }

    private fun setup(){
        button_ini_sesion_google = findViewById(R.id.buttonInicgoogle)
        button_ini_sesion_google.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleCliete = GoogleSignIn.getClient(this, googleConf)
            googleCliete.signOut()

            startActivityForResult(googleCliete.signInIntent, GOOGLE_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val acount = task.getResult(ApiException::class.java)
                if (acount != null) {
                    val credencial = GoogleAuthProvider.getCredential(acount.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credencial)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                irInicio(acount.email ?:"", ProviderType.GOOGLE)
                                val nombreTexto = acount.displayName ?: "Nombre no disponible"
                                val correoTexto = acount.email ?: "Correo no disponible"
                                val datosUsuario = hashMapOf(
                                    "nombre" to nombreTexto,
                                    "correo" to correoTexto
                                    // No se guarda la contraseña por seguridad
                                )

                                db.collection("users").document(correoTexto).set(datosUsuario)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                showAlert()
                            }
                        }
                }

            }catch (e: ApiException){
                showAlert()
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
