// XML -> activity_main

package com.example.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Randloses Layout aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val registerButton = findViewById<Button>(R.id.btnStart)
        val loginText = findViewById<TextView>(R.id.txtLogin)


        registerButton.setOnClickListener {
            // Weiterleitung zur Register-Seite
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginText.setOnClickListener {
            // Weiterleitung zur Login-Seite
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}


/*registerButton.setOnClickListener {
    Toast.makeText(this, "RegisterButton funktioniert!", Toast.LENGTH_SHORT).show()
    val intent = Intent(this, RegisterActivity::class.java)
    startActivity(intent)
}*/