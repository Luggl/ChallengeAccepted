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
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import de.thws.challengeaccepted.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class ProfileSettingsActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        prefs = getSharedPreferences("app", MODE_PRIVATE)

        val nameEdit = findViewById<EditText>(R.id.etUserName)
        val userId = prefs.getString("USER_ID", null)

        if (userId != null) {
            userViewModel.getUser(userId) { user ->
                user?.let {
                    nameEdit.setText(it.username)
                }
            }
        }
        val emailBtn = findViewById<Button>(R.id.btnEmail)
        if (userId != null) {
            userViewModel.getUser(userId) { user ->
                user?.let {
                    nameEdit.setText(it.username)
                    emailBtn.text = it.email         // <-- Email dynamisch setzen
                }
            }
        }

        // Name speichern (wie gehabt)
        nameEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && userId != null) {
                val newName = nameEdit.text.toString()
                userViewModel.getUser(userId) { user ->
                    user?.let {
                        if (it.username != newName && newName.isNotBlank()) {
                            val updatedUser = it.copy(username = newName)
                            userViewModel.insertUser(updatedUser)
                            Toast.makeText(this, "Name aktualisiert", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // --- Navigation/Buttons ---

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<Button>(R.id.btn_change_password).setOnClickListener {
            startActivity(Intent(this, ProfileChangePassActivity::class.java))
        }
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

        // Profil löschen (mit Backend-API-Call!)
        findViewById<Button>(R.id.btn_deactivate_delete).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Profil wirklich löschen?")
                .setMessage("Profil wirklich löschen? Das kann nicht rückgängig gemacht werden.")
                .setPositiveButton("Profil löschen") { _, _ ->
                    if (userId != null) {
                        userViewModel.getUser(userId) { user ->
                            user?.let {
                                lifecycleScope.launch {
                                    val api = ApiClient.retrofit.create(UserService::class.java)
                                    try {
                                        // 1. Backend-Delete (suspend, kein Rückgabewert)
                                        api.deleteUser(userId)
                                        // 2. Lokal löschen
                                        userViewModel.deleteUser(it)
                                        // 3. Preferences clearen & zu Login
                                        prefs.edit().clear().apply()
                                        val intent = Intent(this@ProfileSettingsActivity, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    } catch (e: Exception) {
                                        Toast.makeText(this@ProfileSettingsActivity, "Löschen fehlgeschlagen: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
                .setNegativeButton("Abbrechen", null)
                .show()
        }

        // Bottom Navigation (wie gehabt)
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
