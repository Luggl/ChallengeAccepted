package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Systemleisten ausblenden (Full-Screen)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Login-Status prüfen
        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val userId = prefs.getString("USER_ID", null)

        // Falls User bereits eingeloggt → direkt zum Dashboard
        if (token != null && userId != null) {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            finish()
            return
        }

        // Weiterleitung zur Registrierung
        val registerButton = findViewById<Button>(R.id.btnStart)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Weiterleitung zum Login
        val loginText = findViewById<TextView>(R.id.txtLogin)
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
