package com.example.challengeaccepted
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StandardIntensityActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //verknüpft diese Activity mit dem XML Layout
        setContentView(R.layout.activity_standard_intensity)

        //zurück Button
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }
        //bestätige Button
        val btnConfirm = findViewById<ImageButton>(R.id.btn_confirm_selection)
        btnConfirm.setOnClickListener {
            //Startintensität holen
            val startTextView = findViewById<TextView>(R.id.tv_start_main)
            val startValue = startTextView.text.toString().toIntOrNull() ?: 0

            //Endintensität holen
            val endTextView = findViewById<TextView>(R.id.tv_end_main)
            val endValue = endTextView.text.toString().toIntOrNull() ?: 0

            //Test Ausgabe
            Toast.makeText(this, "Start: $startValue, Ende: $endValue", Toast.LENGTH_SHORT).show()
        }
    }
}