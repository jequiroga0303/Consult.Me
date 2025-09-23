package com.example.consultme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class AppointmentDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        // Referencia al botón de regreso
        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDetails)

        // Referencia al botón final para agendar
        val btnSchedule = findViewById<Button>(R.id.btnSchedule)

        // Lógica para el botón de regreso: vuelve a la pantalla de la lista de doctores
        btnBack.setOnClickListener {
            finish()
        }

        // Lógica para el botón de agendar
        btnSchedule.setOnClickListener {
            // Por ahora, simplemente regresa a la pantalla de inicio
            // En el futuro, aquí se agregaría la lógica para guardar la cita en una base de datos.
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}