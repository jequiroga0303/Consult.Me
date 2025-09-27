package com.example.consultme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
class PsychologyActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psychology)

        // Referencia al botón de regreso
        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDoctorList)

        // Lógica para el botón de regreso: vuelve a la pantalla de inicio
        btnBack.setOnClickListener {
            finish()
        }
        loadDoctors("Pyschology")

    }
    private fun loadDoctors(category: String) {
        val doctorsContainer = findViewById<LinearLayout>(R.id.doctors_container)
        doctorsContainer.removeAllViews() // Limpia los datos existentes

        db.collection("doctors")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val doctor = document.toObject(Doctor::class.java)
                    // Inflar un nuevo CardView y llenarlo con los datos del doctor
                    // Aquí necesitarás crear un layout dinámico o duplicar un CardView existente
                    // en tu XML para cada doctor.
                    // Por simplicidad, este ejemplo solo muestra un mensaje
                    val tvDoctorName = TextView(this).apply { text = doctor.name }
                    doctorsContainer.addView(tvDoctorName)
                    // ... Agrega más views para specialty, etc.
                    // Agrega un botón de "Agendar" con un OnClickListener que pase los datos del doctor
                    val btnSchedule = Button(this).apply { text = "Agendar" }
                    btnSchedule.setOnClickListener {
                        val intent = Intent(this@PsychologyActivity, AppointmentDetailsActivity::class.java)
                        intent.putExtra("doctorId", document.id)
                        intent.putExtra("doctorName", doctor.name)
                        startActivity(intent)
                    }
                    doctorsContainer.addView(btnSchedule)
                }
            }
            .addOnFailureListener {
                // Manejar error
            }
    }
}