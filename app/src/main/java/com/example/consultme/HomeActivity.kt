package com.example.consultme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Referencia a los botones de categoría
        val btnMedicalGuidance = findViewById<Button>(R.id.btn_medical_guidance)
        val btnNutrition = findViewById<Button>(R.id.btn_nutrition)
        val btnPsychology = findViewById<Button>(R.id.btn_psychology)
        val btnCoaching = findViewById<Button>(R.id.btn_coaching)
        val btnProfile = findViewById<Button>(R.id.btn_profile)

        // Lógica para el botón de Guianza Médica
        btnMedicalGuidance.setOnClickListener {
            // Inicia la actividad DoctorListActivity
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }

        // Lógica para los otros botones de categoría
        btnNutrition.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }
        btnPsychology.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }
        btnCoaching.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }

        // Lógica para el botón de Perfil
        btnProfile.setOnClickListener {
            // Por ahora, este botón no tiene una funcionalidad, pero podrías
            // añadir la navegación a una pantalla de perfil en el futuro.
        }
    }
}