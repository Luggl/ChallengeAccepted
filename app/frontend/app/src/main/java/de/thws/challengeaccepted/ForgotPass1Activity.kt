// XML -> activity_forgot_pass1

package de.thws.challengeaccepted

    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import android.widget.EditText
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.WindowCompat

    class ForgotPasswordActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            // Randloses Layout aktivieren
            WindowCompat.setDecorFitsSystemWindows(window, false)

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_forgot_pass1)

            val emailInput = findViewById<EditText>(R.id.edit_email)
            val requestButton = findViewById<Button>(R.id.button_request_mail)

            requestButton.setOnClickListener {
                // Email-Reset-Request senden und nicht die nächst Seite verknüpfen!!!
                val intent = Intent(this, ResetPasswordActivity::class.java)
                startActivity(intent)
            }
        }
    }
