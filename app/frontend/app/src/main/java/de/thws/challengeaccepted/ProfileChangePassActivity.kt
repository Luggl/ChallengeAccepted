package de.thws.challengeaccepted

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import kotlinx.coroutines.launch

class ProfileChangePassActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_change_pass)
        prefs = getSharedPreferences("app", MODE_PRIVATE)


        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

// STATUSLEISTE OBEN BEHANDELN (z. B. bei Notch oder Uhrzeit)
        ViewCompat.setOnApplyWindowInsetsListener(rootScroll) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

// NAVIGATIONSBALKEN UNTEN BEHANDELN
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Padding NUR unten – oben fest (z. B. 8dp), unten dynamisch
            view.setPadding(
                view.paddingLeft,
                8.dpToPx(),
                view.paddingRight,
                8.dpToPx(),
            )
            insets
        }

// Optional: Hintergrundfarbe für Navigationsleiste setzen
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)


        // Felder holen
        val oldPasswordEdit = findViewById<EditText>(R.id.oldPasswordEditText)
        val newPasswordEdit = findViewById<EditText>(R.id.newPasswordEditText)
        val repeatPasswordEdit = findViewById<EditText>(R.id.repeatPasswordEditText)
        val changePassBtn = findViewById<Button>(R.id.change_pass)

        // Passwort ändern-Button
        changePassBtn.setOnClickListener {
            val oldPassword = oldPasswordEdit.text.toString()
            val newPassword = newPasswordEdit.text.toString()
            val repeatPassword = repeatPasswordEdit.text.toString()

            // Validierung
            if (oldPassword.isEmpty() || newPassword.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword != repeatPassword) {
                Toast.makeText(this, "Neue Passwörter stimmen nicht überein!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword.length < 6) {
                Toast.makeText(this, "Neues Passwort muss mind. 6 Zeichen lang sein!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Token aus Preferences holen (beim Login speichern!)
            val token = prefs.getString("token", null)
            if (token == null) {
                Toast.makeText(this, "Nicht eingeloggt!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val service = ApiClient.retrofit.create(UserService::class.java)
                try {
                    // PATCH /user/password
                    service.changePassword(
                        token = "Bearer $token",  // <-- wichtig!
                        request = de.thws.challengeaccepted.network.UserService.ChangePasswordRequest(
                            oldPassword = oldPassword,
                            newPassword = newPassword
                        )
                    )
                    Toast.makeText(this@ProfileChangePassActivity, "Passwort erfolgreich geändert!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProfileChangePassActivity, ProfileSettingsActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@ProfileChangePassActivity, "Fehler: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Navigation Back
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            startActivity(Intent(this, ProfileSettingsActivity::class.java))
            finish()
        }

        // Bottom Navigation
        findViewById<ImageView>(R.id.nav_group).setOnClickListener {
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_add).setOnClickListener {
            startActivity(Intent(this, CreateNewGroupActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
