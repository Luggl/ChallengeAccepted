package de.thws.challengeaccepted

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import de.thws.challengeaccepted.models.PasswordResetConfirmRequest
import de.thws.challengeaccepted.models.PasswordResetConfirmResponse
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass2)

        val newPassword = findViewById<EditText>(R.id.edit_password1)
        val repeatPassword = findViewById<EditText>(R.id.edit_password2)
        val changeButton = findViewById<Button>(R.id.button_change_password)

        changeButton.setOnClickListener {
            val pw = newPassword.text.toString()
            val rpw = repeatPassword.text.toString()
            // Den Token solltest du z.B. über einen Intent-Extra bekommen!
            val token = "" // TODO: Token aus Intent holen

            if (pw.isEmpty() || rpw.isEmpty()) {
                Toast.makeText(this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw != rpw) {
                Toast.makeText(this, "Passwörter stimmen nicht überein!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            confirmPasswordReset(token, pw)
        }
    }

    private fun confirmPasswordReset(token: String, password: String) {
        val service = ApiClient.retrofit.create(UserService::class.java)
        val request = PasswordResetConfirmRequest(token, password)
        val call = service.confirmPasswordReset(request)
        call.enqueue(object : Callback<PasswordResetConfirmResponse> {
            override fun onResponse(call: Call<PasswordResetConfirmResponse>, response: Response<PasswordResetConfirmResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@ResetPasswordActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                    // Optional: zurück zum Login
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Fehler beim Zurücksetzen!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PasswordResetConfirmResponse>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
