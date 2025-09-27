package com.example.consultme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppointmentDetailsActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView
    private lateinit var selectedCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        selectedCalendar = Calendar.getInstance()

        val doctorId = intent.getStringExtra("doctorId") ?: ""

        // Llenar la información del doctor desde Firestore
        loadDoctorDetails(doctorId)

        // Configurar la selección de fecha y hora
        tvSelectedDate.setOnClickListener { showDatePickerDialog() }
        tvSelectedTime.setOnClickListener { showTimePickerDialog() }

        // ... Lógica para agendar la cita
        val btnSchedule = findViewById<Button>(R.id.btnSchedule)
        btnSchedule.setOnClickListener {
            // Lógica para guardar la cita en Firestore
            if (auth.currentUser != null && doctorId.isNotEmpty()) {
                val appointment = hashMapOf(
                    "userId" to auth.currentUser!!.uid,
                    "doctorId" to doctorId,
                    "date" to SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es", "MX")).format(selectedCalendar.time),
                    "time" to SimpleDateFormat("hh:mm a", Locale.US).format(selectedCalendar.time),
                    "timestamp" to selectedCalendar.time,
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

    private fun loadDoctorDetails(doctorId: String) {
        db.collection("doctors").document(doctorId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val doctor = document.toObject(Doctor::class.java)
                    if (doctor != null) {
                        findViewById<TextView>(R.id.tvDoctorName).text = doctor.name
                        findViewById<TextView>(R.id.tvDoctorSpecialty).text = doctor.specialty
                        findViewById<TextView>(R.id.tvDoctorStudies).text = doctor.studies
                        findViewById<TextView>(R.id.tvDoctorExperience).text = doctor.experience
                    }
                }
            }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            selectedCalendar.set(Calendar.YEAR, year)
            selectedCalendar.set(Calendar.MONTH, monthOfYear)
            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateLabel()
        }
        DatePickerDialog(this, dateSetListener,
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePickerDialog() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedCalendar.set(Calendar.MINUTE, minute)
            updateTimeLabel()
        }
        TimePickerDialog(this, timeSetListener,
            selectedCalendar.get(Calendar.HOUR_OF_DAY),
            selectedCalendar.get(Calendar.MINUTE), false).show()
    }

    private fun updateDateLabel() {
        val format = "dd 'de' MMMM 'del' yyyy"
        val sdf = SimpleDateFormat(format, Locale("es", "MX"))
        tvSelectedDate.text = sdf.format(selectedCalendar.time)
    }

    private fun updateTimeLabel() {
        val format = "hh:mm a"
        val sdf = SimpleDateFormat(format, Locale.US)
        tvSelectedTime.text = sdf.format(selectedCalendar.time)
    }
}