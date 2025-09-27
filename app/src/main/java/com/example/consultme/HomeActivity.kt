package com.example.consultme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Typeface

class HomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnMedicalGuidance = findViewById<Button>(R.id.btn_medical_guidance)
        val btnNutrition = findViewById<Button>(R.id.btn_nutrition)
        val btnPsychology = findViewById<Button>(R.id.btn_psychology)
        val btnCoaching = findViewById<Button>(R.id.btn_coaching)
        val btnProfile = findViewById<Button>(R.id.btn_profile)

        btnMedicalGuidance.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }

        btnNutrition.setOnClickListener {
            val intent = Intent(this, NutritionActivity::class.java)
            startActivity(intent)
        }

        btnPsychology.setOnClickListener {
            val intent = Intent(this, PsychologyActivity::class.java)
            startActivity(intent)
        }

        btnCoaching.setOnClickListener {
            val intent = Intent(this, CoachingActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            // Lógica del perfil
        }
        loadScheduledAppointments()
    }

    private fun loadScheduledAppointments() {
        val consultationsContainer = findViewById<LinearLayout>(R.id.consultations_container)
        consultationsContainer.removeAllViews()

        val userId = auth.currentUser?.uid
        if (userId == null) {
            val noAppointmentsText = TextView(this).apply {
                text = "No hay consultas agendadas por el momento."
                gravity = Gravity.CENTER
                setTextColor(resources.getColor(R.color.black, null))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
            consultationsContainer.addView(noAppointmentsText)
            return
        }

        db.collection("appointments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val noAppointmentsText = TextView(this).apply {
                        text = "No hay consultas agendadas por el momento."
                        gravity = Gravity.CENTER
                        setTextColor(resources.getColor(R.color.black, null))
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    }
                    consultationsContainer.addView(noAppointmentsText)
                } else {
                    for (appointmentDocument in documents) {
                        val doctorId = appointmentDocument.getString("doctorId") ?: continue
                        val date = appointmentDocument.getString("date")
                        val time = appointmentDocument.getString("time")

                        db.collection("doctors").document(doctorId).get()
                            .addOnSuccessListener { doctorDocument ->
                                val doctorName = doctorDocument.getString("name") ?: "Doctor no disponible"
                                val videoCallUrl = doctorDocument.getString("videoCallUrl")

                                val cardView = CardView(this).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        setMargins(0, 0, 0, 16)
                                    }
                                    radius = 12f
                                }

                                val appointmentLayout = LinearLayout(this).apply {
                                    orientation = LinearLayout.HORIZONTAL
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                    setPadding(16, 16, 16, 16)
                                }

                                val textLayout = LinearLayout(this).apply {
                                    orientation = LinearLayout.VERTICAL
                                    layoutParams = LinearLayout.LayoutParams(
                                        0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1.0f
                                    )
                                }

                                val tvAppointmentTitle = TextView(this).apply {
                                    text = "Cita con $doctorName"
                                    textSize = 16f
                                    typeface = Typeface.create(typeface, Typeface.BOLD)
                                }
                                val tvAppointmentDetails = TextView(this).apply {
                                    text = "Fecha: $date\nHora: $time"
                                    textSize = 14f
                                }
                                textLayout.addView(tvAppointmentTitle)
                                textLayout.addView(tvAppointmentDetails)

                                val joinButton = Button(this).apply {
                                    text = "Unirse"
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    setBackgroundColor(resources.getColor(R.color.white, null))
                                    setTextColor(resources.getColor(R.color.black, null))
                                    setOnClickListener {
                                        if (!videoCallUrl.isNullOrEmpty()) {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoCallUrl))
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(this@HomeActivity, "URL de videollamada no disponible.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                                appointmentLayout.addView(textLayout)
                                appointmentLayout.addView(joinButton)
                                cardView.addView(appointmentLayout)
                                consultationsContainer.addView(cardView)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar citas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}