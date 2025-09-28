// Archivo: app/src/main/java/com/example/consultme/AppointmentDetailsActivity.kt

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
    private lateinit var tvDoctorName: TextView
    private lateinit var tvDoctorSpecialty: TextView
    private lateinit var tvDoctorStudies: TextView
    private lateinit var tvDoctorExperience: TextView
    private lateinit var tvAppointmentCost: TextView
    private lateinit var tvDetailsDate: TextView
    private lateinit var tvDetailsTime: TextView
    private var selectedCalendar: Calendar = Calendar.getInstance()
    private var doctorId: String = ""
    private var doctorName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        // Inicializar vistas
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvDoctorSpecialty = findViewById(R.id.tvDoctorSpecialty)
        tvDoctorStudies = findViewById(R.id.tvDoctorStudies)
        tvDoctorExperience = findViewById(R.id.tvDoctorExperience)
        tvAppointmentCost = findViewById(R.id.tvAppointmentCost)
        tvDetailsDate = findViewById(R.id.tvDetailsDate)
        tvDetailsTime = findViewById(R.id.tvDetailsTime)

        doctorId = intent.getStringExtra("doctorId") ?: ""

        // Cargar los detalles del doctor desde Firestore
        loadDoctorDetails()
        updateSummaryDetails()

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDetails)
        btnBack.setOnClickListener { finish() }

        tvSelectedDate.setOnClickListener { showDatePickerDialog() }
        tvSelectedTime.setOnClickListener { showTimePickerDialog() }

        val btnSchedule = findViewById<Button>(R.id.btnSchedule)
        btnSchedule.setOnClickListener {
            if (auth.currentUser != null) {
                // Obtener la fecha y hora seleccionada como un objeto Date
                val selectedDateTime = selectedCalendar.time

                // Definir un rango de 45 minutos antes y después de la nueva cita para el chequeo
                val checkStart = Calendar.getInstance().apply {
                    time = selectedDateTime
                    add(Calendar.MINUTE, -44)
                }.time
                val checkEnd = Calendar.getInstance().apply {
                    time = selectedDateTime
                    add(Calendar.MINUTE, 44)
                }.time

                // Consultar citas existentes para el doctor en ese rango de tiempo
                db.collection("appointments")
                    .whereEqualTo("doctorId", doctorId)
                    .whereGreaterThanOrEqualTo("timestamp", checkStart)
                    .whereLessThanOrEqualTo("timestamp", checkEnd)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // No hay citas que se traslapen, agendar la nueva
                            val appointment = hashMapOf(
                                "userId" to auth.currentUser!!.uid,
                                "doctorId" to doctorId,
                                "doctorName" to doctorName,
                                "date" to SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es", "MX")).format(selectedDateTime),
                                "time" to SimpleDateFormat("hh:mm a", Locale.US).format(selectedDateTime),
                                "timestamp" to selectedDateTime
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
                            // Ya existe una cita en ese rango, mostrar un error
                            Toast.makeText(this, "Horario no disponible. Por favor, selecciona otra hora.", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al verificar la disponibilidad: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Inicia sesión para agendar una cita.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDoctorDetails() {
        if (doctorId.isNotEmpty()) {
            db.collection("doctors").document(doctorId).get()
                .addOnSuccessListener { document ->
                    val doctor = document.toObject(Doctor::class.java)
                    if (doctor != null) {
                        doctorName = doctor.name
                        tvDoctorName.text = doctor.name
                        tvDoctorSpecialty.text = doctor.specialty
                        tvDoctorStudies.text = doctor.studies
                        tvDoctorExperience.text = doctor.experience
                        tvAppointmentCost.text = "- Cobro por cita: ${doctor.costoConsulta}"
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
            updateSummaryDetails()
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
            updateSummaryDetails()
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
    private fun updateSummaryDetails() {
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es", "MX"))
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        tvDetailsDate.text = "- Fecha: ${dateFormat.format(selectedCalendar.time)}"
        tvDetailsTime.text = "- Hora: ${timeFormat.format(selectedCalendar.time)} (Duración máx.: 45 min)"
    }
}