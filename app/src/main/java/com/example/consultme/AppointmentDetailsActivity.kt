package com.example.consultme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AppointmentDetailsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedDate: String = "20 de Abril del 2025" // Lógica para seleccionar fecha
    private var selectedTime: String = "9:40 am" // Lógica para seleccionar hora

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        val doctorId = intent.getStringExtra("doctorId") ?: ""
        val doctorName = intent.getStringExtra("doctorName") ?: ""

        val tvDoctorName = findViewById<TextView>(R.id.tvDoctorName)
        tvDoctorName.text = doctorName

        // Referencia al botón de regreso
        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDetails)
        // Lógica para el botón de regreso: vuelve a la pantalla de la lista de doctores
        btnBack.setOnClickListener {
            finish()
        }
        val btnSchedule = findViewById<Button>(R.id.btnSchedule)
        btnSchedule.setOnClickListener {
            if (auth.currentUser != null) {
                val appointment = hashMapOf(
                    "userId" to auth.currentUser!!.uid,
                    "doctorId" to doctorId,
                    "doctorName" to doctorName,
                    "date" to selectedDate,
                    "time" to selectedTime,
                    "timestamp" to Date()
                )
                db.collection("appointments")
                    .add(appointment)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cita agendada exitosamente!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al agendar cita: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Inicia sesión para agendar una cita.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}