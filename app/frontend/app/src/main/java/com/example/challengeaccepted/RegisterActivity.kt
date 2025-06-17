// XML -> activity_register

package com.example.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class RegisterActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        // Randloses Layout aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val username = findViewById<EditText>(R.id.edit_username)
        val email = findViewById<EditText>(R.id.edit_email)
        val password = findViewById<EditText>(R.id.edit_password1)
        val repeatPassword = findViewById<EditText>(R.id.edit_password2)
        val registerButton = findViewById<Button>(R.id.button_start_regist)
        val alreadyAccount = findViewById<TextView>(R.id.text_already_account)

        registerButton.setOnClickListener {

            val user = username.text.toString()
            val mail = email.text.toString()
            val pw = password.text.toString()
            val rpw = repeatPassword.text.toString()

            if (user.isNotEmpty() && mail.isNotEmpty() && pw.isNotEmpty() && rpw.isNotEmpty()) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }

            alreadyAccount.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}