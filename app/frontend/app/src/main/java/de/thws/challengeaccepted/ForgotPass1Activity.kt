package de.thws.challengeaccepted

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import de.thws.challengeaccepted.models.PasswordResetRequest
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass1)

        val emailInput = findViewById<EditText>(R.id.edit_email)
        val requestButton = findViewById<Button>(R.id.button_request_mail)

        requestButton.setOnClickListener {
            val email = emailInput.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Bitte E-Mail eingeben!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            requestPasswordReset(email)
        }
    }

    private fun requestPasswordReset(email: String) {
        val service = ApiClient.retrofit.create(UserService::class.java)
        val request = PasswordResetRequest(email)

        // Der moderne Coroutine-Aufruf:
        lifecycleScope.launch {
            try {
                val response = service.requestPasswordReset(request)
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    response.message,
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Netzwerkfehler: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
