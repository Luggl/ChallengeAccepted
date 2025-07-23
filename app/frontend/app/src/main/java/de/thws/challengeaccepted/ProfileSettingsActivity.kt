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
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import de.thws.challengeaccepted.data.database.AppDatabase
import de.thws.challengeaccepted.data.repository.UserRepository
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import de.thws.challengeaccepted.ui.viewmodels.UserViewModel
import de.thws.challengeaccepted.ui.viewmodels.UserViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileSettingsActivity : AppCompatActivity() {

    // ViewModel mit Factory initialisieren (Repository aus Retrofit + Room)
    private val userViewModel: UserViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val userService = ApiClient.getRetrofit(applicationContext).create(UserService::class.java)
        val repository = UserRepository(userService, db.userDao())
        UserViewModelFactory(repository)
    }

    private lateinit var prefs: SharedPreferences

    // Hilfsfunktion für dp → px
    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        prefs = getSharedPreferences("app", MODE_PRIVATE)

        // Randloses Layout aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        // Obere Systemleiste (z. B. Notch) berücksichtigen
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

        // Untere Systemleiste (Navigation Bar) berücksichtigen
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            view.setPadding(
                view.paddingLeft,
                8.dpToPx(),
                view.paddingRight,
                8.dpToPx(),
            )
            insets
        }

        // Navigationsleiste einfärben
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // UI-Elemente referenzieren
        val nameEdit = findViewById<EditText>(R.id.etUserName)
        val emailBtn = findViewById<Button>(R.id.btnEmail)
        val userId = prefs.getString("USER_ID", null)

        // Ohne USER_ID → abbrechen
        if (userId == null) {
            Toast.makeText(this, "Fehler: Nicht angemeldet.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Nutzerdaten laden und UI füllen
        userViewModel.loadInitialData(userId)
        lifecycleScope.launch {
            val user = userViewModel.user.first { it != null }
            user?.let {
                nameEdit.setText(it.username)
                emailBtn.text = it.email
            }
        }

        // Namensänderung speichern, sobald das Feld den Fokus verliert
        nameEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val currentUser = userViewModel.user.value
                val newName = nameEdit.text.toString()
                if (currentUser != null && currentUser.username != newName && newName.isNotBlank()) {
                    val updatedUser = currentUser.copy(username = newName)
                    userViewModel.updateUser(updatedUser)
                    Toast.makeText(this, "Name aktualisiert", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Buttons & Navigation einrichten
        setupNavigationAndButtons(userId)
    }

    // Bestätigungsdialog für Profil-Löschung
    private fun showDeleteConfirmationDialog(userId: String) {
        AlertDialog.Builder(this)
            .setTitle("Profil wirklich löschen?")
            .setMessage("Das kann nicht rückgängig gemacht werden.")
            .setPositiveButton("Profil löschen") { _, _ ->
                userViewModel.deleteCurrentUser(
                    userId,
                    onSuccess = {
                        // SharedPreferences und Datenbank löschen
                        prefs.edit().clear().apply()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(this, "Löschen fehlgeschlagen: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                )
            }
            .setNegativeButton("Abbrechen", null)
            .show()
    }

    // Alle Button- und Navigationslistener setzen
    private fun setupNavigationAndButtons(userId: String) {
        // Zurück zur Profilseite
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Passwort ändern
        findViewById<Button>(R.id.btn_change_password).setOnClickListener {
            startActivity(Intent(this, ProfileChangePassActivity::class.java))
        }

        // Datenschutzseite öffnen
        findViewById<Button>(R.id.btn_datenschutz).setOnClickListener {
            startActivity(Intent(this, DataPrivacyActivity::class.java))
        }

        // Logout mit Bestätigung
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Wirklich abmelden?")
                .setPositiveButton("Abmelden") { _, _ ->
                    lifecycleScope.launch {
                        // Datenbank und SharedPrefs löschen
                        AppDatabase.getDatabase(applicationContext).clearAllData()
                        prefs.edit().clear().apply()

                        // Navigation zum Login
                        val intent = Intent(this@ProfileSettingsActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                .setNegativeButton("Abbrechen", null)
                .show()
        }

        // Profil löschen mit Bestätigungsdialog
        findViewById<Button>(R.id.btn_deactivate_delete).setOnClickListener {
            showDeleteConfirmationDialog(userId)
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
