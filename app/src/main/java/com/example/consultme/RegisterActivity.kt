package com.example.consultme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.text.method.LinkMovementMethod

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        val btnRegisterComplete = findViewById<Button>(R.id.btnRegisterComplete)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val etEmailRegister = findViewById<EditText>(R.id.etEmailRegister)
        val etPasswordRegister = findViewById<EditText>(R.id.etPasswordRegister)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val tvTermsLink = findViewById<TextView>(R.id.tvTermsLink)
        val termsScrollView = findViewById<ScrollView>(R.id.terms_scroll_view)
        val tvTermsContent = findViewById<TextView>(R.id.tvTermsContent)

        // Hacer que el TextView de los términos sea scrollable si el contenido es largo
        tvTermsContent.movementMethod = ScrollingMovementMethod()
        // Mostrar el contenido de los términos como HTML
        tvTermsContent.text = Html.fromHtml(getString(R.string.terms_and_conditions), Html.FROM_HTML_MODE_LEGACY)

        // Lógica para desplegar/esconder los términos
        tvTermsLink.setOnClickListener {
            if (termsScrollView.visibility == View.VISIBLE) {
                termsScrollView.visibility = View.GONE
            } else {
                termsScrollView.visibility = View.VISIBLE
            }
        }

        btnRegisterComplete.setOnClickListener {
            val email = etEmailRegister.text.toString()
            val password = etPasswordRegister.text.toString()

            if (!cbTerms.isChecked) {
                Toast.makeText(this, "Debes aceptar los términos y condiciones.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error de registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        btnBack.setOnClickListener {
            finish()
        }
    }
}