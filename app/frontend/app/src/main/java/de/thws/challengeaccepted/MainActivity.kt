// XML -> activity_main

package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import de.thws.challengeaccepted.data.entities.User
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerButton = findViewById<Button>(R.id.btnStart)
        val loginText = findViewById<TextView>(R.id.txtLogin)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Room-Datenbank-Test (Einmalig!)
        lifecycleScope.launch {
            // Test-User speichern
            val user = User(
                userId = "test-uuid",
                username = "Moritz",
                email = "moritz@example.com",
                streak = 10,
                profilbild = null
            )
            App.database.userDao().insertUser(user)

            // User aus DB abrufen und in Log ausgeben
            val storedUser = App.database.userDao().getUser("test-uuid")
            storedUser?.let {
                Toast.makeText(
                    this@MainActivity,
                    "User aus DB: ${it.username}, Streak: ${it.streak}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}