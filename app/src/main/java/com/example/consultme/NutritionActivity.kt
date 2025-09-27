package com.example.consultme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import android.view.LayoutInflater

class NutritionActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition)

        // Referencia al botón de regreso
        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDoctorList)
        btnBack.setOnClickListener {
            finish()
        }
        loadDoctors("Nutrition")
    }
    private fun loadDoctors(category: String) {
        val doctorsContainer = findViewById<LinearLayout>(R.id.doctors_container)
        doctorsContainer.removeAllViews()

        db.collection("doctors")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val doctor = document.toObject(Doctor::class.java)

                    // 1. Inflar el layout de la tarjeta
                    val doctorCardView = LayoutInflater.from(this)
                        .inflate(R.layout.doctor_card_item, doctorsContainer, false)

                    // 2. Encontrar las vistas dentro de la tarjeta
                    val tvDoctorName = doctorCardView.findViewById<TextView>(R.id.doctor_name)
                    val tvDoctorSpecialty = doctorCardView.findViewById<TextView>(R.id.doctor_specialty)
                    val btnSchedule = doctorCardView.findViewById<Button>(R.id.btnSchedule)

                    // 3. Llenar los datos de la tarjeta con la información de Firestore
                    tvDoctorName.text = doctor.name
                    tvDoctorSpecialty.text = doctor.specialty

                    // 4. Configurar el botón de agendar
                    btnSchedule.setOnClickListener {
                        val intent = Intent(this, AppointmentDetailsActivity::class.java)
                        intent.putExtra("doctorId", document.id)
                        startActivity(intent)
                    }

                    // 5. Agregar la tarjeta al contenedor
                    doctorsContainer.addView(doctorCardView)
                }
            }
            .addOnFailureListener {
                // Manejar error
            }
    }
}