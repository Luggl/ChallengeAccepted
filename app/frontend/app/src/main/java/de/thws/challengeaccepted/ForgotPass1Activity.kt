package de.thws.challengeaccepted

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import de.thws.challengeaccepted.models.PasswordResetRequest
import de.thws.challengeaccepted.models.PasswordResetResponse
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            // Optional: E-Mail-Format prüfen!
            // Email-Reset-Request senden
            requestPasswordReset(email)
        }
    }

    private fun requestPasswordReset(email: String) {
        val service = ApiClient.retrofit.create(UserService::class.java)
        val request = PasswordResetRequest(email)
        val call = service.requestPasswordReset(request)
        call.enqueue(object : Callback<PasswordResetResponse> {
            override fun onResponse(call: Call<PasswordResetResponse>, response: Response<PasswordResetResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@ForgotPasswordActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                    // Hier KEIN automatisches Weiterleiten!
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Fehler beim Senden!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PasswordResetResponse>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
