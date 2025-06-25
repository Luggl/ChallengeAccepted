// XML -> activity_forgot_pass2

package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Randloses Layout aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass2)

        val newPassword = findViewById<EditText>(R.id.edit_password1)
        val repeatPassword = findViewById<EditText>(R.id.edit_password2)
        val changeButton = findViewById<Button>(R.id.button_change_password)

        changeButton.setOnClickListener {
            // TODO: Passwort speichern und ggf. Feedback geben
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
