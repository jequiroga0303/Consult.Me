package com.example.consultme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class DoctorListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        // Referencia al botón de regreso
        val btnBack = findViewById<ImageButton>(R.id.btnBackFromDoctorList)

        // Referencia a los botones de "Agendar"
        val btnScheduleAlejandra = findViewById<Button>(R.id.btnScheduleAlejandra)
        val btnScheduleMariana = findViewById<Button>(R.id.btnScheduleMariana)

        // Lógica para el botón de regreso: vuelve a la pantalla de inicio
        btnBack.setOnClickListener {
            finish()
        }

        // Lógica para el botón de agendar de la Dra. Alejandra Guzmán
        btnScheduleAlejandra.setOnClickListener {
            // Navega a la pantalla de detalles de la cita
            val intent = Intent(this, AppointmentDetailsActivity::class.java)
            startActivity(intent)
        }

        // Lógica para el botón de agendar de la Dra. Mariana López
        btnScheduleMariana.setOnClickListener {
            // Navega a la pantalla de detalles de la cita
            val intent = Intent(this, AppointmentDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}