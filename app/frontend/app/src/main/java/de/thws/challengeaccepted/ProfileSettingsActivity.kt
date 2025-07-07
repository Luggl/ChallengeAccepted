package de.thws.challengeaccepted

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

    private val userViewModel: UserViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val userService = ApiClient.getRetrofit(applicationContext).create(UserService::class.java)
        val repository = UserRepository(userService, db.userDao())
        // GEÄNDERT: Übergib hier nur noch das Repository
        UserViewModelFactory(repository)
    }
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        prefs = getSharedPreferences("app", MODE_PRIVATE)

        val nameEdit = findViewById<EditText>(R.id.etUserName)
        val emailBtn = findViewById<Button>(R.id.btnEmail)
        val userId = prefs.getString("USER_ID", null)

        if (userId == null) {
            Toast.makeText(this, "Fehler: Nicht angemeldet.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Daten laden und UI füllen
        userViewModel.loadInitialData(userId)
        lifecycleScope.launch {
            val user = userViewModel.user.first { it != null }
            user?.let {
                nameEdit.setText(it.username)
                emailBtn.text = it.email
            }
        }

        // Name speichern, wenn der Fokus verloren geht
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

        // Navigation und Button-Listener initialisieren
        setupNavigationAndButtons(userId)
    }

    private fun showDeleteConfirmationDialog(userId: String) {
        AlertDialog.Builder(this)
            .setTitle("Profil wirklich löschen?")
            .setMessage("Das kann nicht rückgängig gemacht werden.")
            .setPositiveButton("Profil löschen") { _, _ ->
                userViewModel.deleteCurrentUser(userId,
                    onSuccess = {
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

    private fun setupNavigationAndButtons(userId: String) {
        // Zurück-Button
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Passwort ändern
        findViewById<Button>(R.id.btn_change_password).setOnClickListener {
            startActivity(Intent(this, ProfileChangePassActivity::class.java))
        }

        // Datenschutz
        findViewById<Button>(R.id.btn_datenschutz).setOnClickListener {
            startActivity(Intent(this, DataPrivacyActivity::class.java))
        }

        // Abmelden
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("Wirklich abmelden?")
                .setPositiveButton("Abmelden") { _, _ ->
                    prefs.edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Abbrechen", null)
                .show()
        }

        // Profil löschen
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