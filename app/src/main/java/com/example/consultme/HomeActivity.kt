package com.example.consultme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri
import android.widget.Toast

class HomeActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

        // Lógica para el botón de Perfil
        btnProfile.setOnClickListener {
            // Por ahora, este botón no tiene una funcionalidad, pero podrías
            // añadir la navegación a una pantalla de perfil en el futuro.
        }
        loadScheduledAppointments()
    }
    private fun loadScheduledAppointments() {
        val consultationsContainer = findViewById<LinearLayout>(R.id.consultations_container)
        consultationsContainer.removeAllViews()

        val userId = auth.currentUser?.uid ?: return

        db.collection("appointments")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp") // Ordenar por fecha para ver las citas cronológicamente
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val noAppointmentsText = TextView(this).apply {
                        text = "No hay consultas agendadas por el momento."
                        gravity = android.view.Gravity.CENTER
                        setTextColor(resources.getColor(R.color.black, null))
                    }
                    consultationsContainer.addView(noAppointmentsText)
                } else {
                    for (document in documents) {
                        val doctorId = document.getString("doctorId") ?: continue
                        val date = document.getString("date")
                        val time = document.getString("time")

                        // Ahora, hacemos una nueva consulta para obtener el nombre y la URL del doctor
                        db.collection("doctors").document(doctorId).get()
                            .addOnSuccessListener { doctorDocument ->
                                val doctorName = doctorDocument.getString("name")
                                val videoCallUrl = doctorDocument.getString("videoCallUrl")

                                val appointmentText = TextView(this).apply {
                                    text = "Cita con $doctorName el $date a las $time"
                            setTextColor(resources.getColor(R.color.black, null))
                            layoutParams = LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1.0f
                            )
                        }
                                val joinButton = Button(this).apply {
                                    text = "Unirse"
                                    setOnClickListener {
                                        if (videoCallUrl != null && videoCallUrl.isNotEmpty()) {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoCallUrl))
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(this@HomeActivity, "URL de videollamada no disponible.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                val appointmentLayout = LinearLayout(this).apply {
                                    orientation = LinearLayout.HORIZONTAL
                                    addView(appointmentText)
                                    addView(joinButton)
                                }
                                consultationsContainer.addView(appointmentLayout)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Manejar error
                Toast.makeText(this, "Error al cargar citas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}