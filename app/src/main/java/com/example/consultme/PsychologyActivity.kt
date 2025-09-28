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
import android.widget.SearchView

class PsychologyActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psychology)

        // Referencia al botón de regreso
        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDoctorList)

        // Lógica para el botón de regreso: vuelve a la pantalla de inicio
        btnBack.setOnClickListener {
            finish()
        }

        searchView = findViewById(R.id.svDoctorSearch)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadDoctors("Psychology", query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                loadDoctors("Psychology", newText)
                return false
            }
        })

        loadDoctors("Psychology", null)

    }
    private fun loadDoctors(category: String, searchQuery: String?) {
        val doctorsContainer = findViewById<LinearLayout>(R.id.doctors_container)
        doctorsContainer.removeAllViews()

        var query = db.collection("doctors")
            .whereEqualTo("category", category)

        if (!searchQuery.isNullOrEmpty()) {
            query = query
                .whereGreaterThanOrEqualTo("name", searchQuery)
                .whereLessThanOrEqualTo("name", searchQuery + "\uf8ff")
        }

        query.get()
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