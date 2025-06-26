package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import de.thws.challengeaccepted.models.LoginRequest
import de.thws.challengeaccepted.models.LoginResponse
import de.thws.challengeaccepted.models.toRoomUser    // <-- Mapping importieren!
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
                loginUser(user, pw)
            } else {
                Toast.makeText(this, "Bitte beide Felder ausfüllen", Toast.LENGTH_SHORT).show()
            }
        }

        forgotText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val service = ApiClient.retrofit.create(UserService::class.java)
        val loginRequest = LoginRequest(email = email, password = password)

        lifecycleScope.launch {
            try {
                val response = service.loginUser(loginRequest)
                if (response.access_token != null && response.user != null) {
                    // User lokal speichern
                    val userEntity = response.user.toRoomUser()
                    App.database.userDao().insertUser(userEntity)
                    val prefs = getSharedPreferences("app", MODE_PRIVATE)
                    prefs.edit().putString("token", response.access_token).apply()
                    prefs.edit().putString("USER_ID", userEntity.userId).apply()
                    prefs.edit().putString("token", response.access_token).apply()

                    // USER_ID an Dashboard übergeben!
                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                    intent.putExtra("USER_ID", userEntity.userId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login fehlgeschlagen!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Netzwerkfehler: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
