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

class NutritionActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var searchView: SearchView
    private var allDoctors = mutableListOf<Doctor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition)

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDoctorList)
        btnBack.setOnClickListener {
            finish()
        }

        searchView = findViewById(R.id.svDoctorSearch)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDoctors(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDoctors(newText)
                return false
            }
        })
        loadAllDoctors("Nutrition")
    }

    private fun loadAllDoctors(category: String) {
        db.collection("doctors")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                allDoctors.clear()
                for (document in documents) {
                    val doctor = document.toObject(Doctor::class.java).apply {
                        this.id = document.id
                    }
                    allDoctors.add(doctor)
                }
                displayDoctors(allDoctors)
            }
            .addOnFailureListener {
                // Manejar error
            }
    }

    private fun filterDoctors(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            allDoctors
        } else {
            allDoctors.filter {
                it.name.contains(query, ignoreCase = true) || it.specialty.contains(query, ignoreCase = true)
            }
        }
        displayDoctors(filteredList)
    }

    private fun displayDoctors(doctors: List<Doctor>) {
        val doctorsContainer = findViewById<LinearLayout>(R.id.doctors_container)
        doctorsContainer.removeAllViews()

        if (doctors.isEmpty()) {
            val noResultsText = TextView(this).apply {
                text = "No se encontraron resultados."
                textSize = 16f
                setTextColor(resources.getColor(R.color.black, null))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            doctorsContainer.addView(noResultsText)
            return
        }

        for (doctor in doctors) {
            val doctorCardView = LayoutInflater.from(this)
                .inflate(R.layout.doctor_card_item, doctorsContainer, false)

            val tvDoctorName = doctorCardView.findViewById<TextView>(R.id.doctor_name)
            val tvDoctorSpecialty = doctorCardView.findViewById<TextView>(R.id.doctor_specialty)
            val btnSchedule = doctorCardView.findViewById<Button>(R.id.btnSchedule)

            tvDoctorName.text = doctor.name
            tvDoctorSpecialty.text = doctor.specialty

            btnSchedule.setOnClickListener {
                val intent = Intent(this, AppointmentDetailsActivity::class.java)
                intent.putExtra("doctorId", doctor.id)
                startActivity(intent)
            }
            doctorsContainer.addView(doctorCardView)
        }
    }
}