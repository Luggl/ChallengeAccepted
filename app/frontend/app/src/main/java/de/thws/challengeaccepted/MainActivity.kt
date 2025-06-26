package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Session-Check: Ist der User noch angemeldet?
        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val userId = prefs.getString("USER_ID", null)

        if (token != null && userId != null) {
            // User ist noch angemeldet → direkt ins Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            // (Optional, falls Dashboard noch User-ID braucht)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            finish()
            return
        }

        // Sonst wie gehabt:
        val registerButton = findViewById<Button>(R.id.btnStart)
        val loginText = findViewById<TextView>(R.id.txtLogin)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
