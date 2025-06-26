package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import de.thws.challengeaccepted.models.RegisterRequest
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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

            // Felder validieren
            if (user.isEmpty() || mail.isEmpty() || pw.isEmpty() || rpw.isEmpty()) {
                Toast.makeText(this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                Toast.makeText(this, "Bitte eine gültige E-Mail-Adresse eingeben!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw != rpw) {
                Toast.makeText(this, "Passwörter stimmen nicht überein!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Jetzt API-Aufruf
            registerUser(user, mail, pw)
        }

        alreadyAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        val service = ApiClient.retrofit.create(UserService::class.java)
        val request = RegisterRequest(username, email, password)

        lifecycleScope.launch {
            try {
                service.registerUser(request) // suspend! Kein Rückgabewert nötig.
                Toast.makeText(
                    this@RegisterActivity,
                    "Registrierung erfolgreich!",
                    Toast.LENGTH_SHORT
                ).show()
                // Optional: Direkt zum Login weiterleiten
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Registrierung fehlgeschlagen: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
