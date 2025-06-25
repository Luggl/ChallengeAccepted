// XML -> activity_login

package de.thws.challengeaccepted


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import de.thws.challengeaccepted.models.LoginRequest
import de.thws.challengeaccepted.models.LoginResponse
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val call = service.loginUser(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.access_token != null) {
                    // Token speichern, wenn du willst:
                    // val token = response.body()!!.access_token
                    // getSharedPreferences("app", MODE_PRIVATE).edit().putString("token", token).apply()

                    val intent = Intent(this@LoginActivity, CreateChallengeModeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login fehlgeschlagen!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
