// XML -> activity_login

package com.example.challengeaccepted


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Randloses Layout aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.button_login)
        val username = findViewById<EditText>(R.id.edit_username)
        val password = findViewById<EditText>(R.id.edit_password)
        val forgotText = findViewById<TextView>(R.id.text_forgot)

        loginButton.setOnClickListener {
            val user = username.text.toString()
            val pw = password.text.toString()

            if (user.isNotEmpty() && pw.isNotEmpty()) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
        }

        forgotText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
