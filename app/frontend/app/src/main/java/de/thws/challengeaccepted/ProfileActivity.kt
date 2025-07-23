package de.thws.challengeaccepted

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    // ViewModel mit Repository initialisieren (Datenbank & API)
    private val userViewModel: UserViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val userService = ApiClient.getRetrofit(applicationContext).create(UserService::class.java)
        val repository = UserRepository(userService, db.userDao())
        // Übergabe des Repositories an das ViewModel
        UserViewModelFactory(repository)
    }

    // Hilfsfunktion: dp → px
    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Vollbildmodus – Inhalte hinter Status- und Navigationsleisten erlauben
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        // Statusleiste oben einbeziehen (z. B. Notch, Uhrzeit)
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

        // Navigationsleiste unten einbeziehen
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Nur Padding unten dynamisch setzen
            view.setPadding(
                view.paddingLeft,
                8.dpToPx(),
                view.paddingRight,
                8.dpToPx()
            )
            insets
        }

        // Optional: Navigationsleiste einfärben
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // Views für Benutzername & Streak holen
        val nameView = findViewById<TextView>(R.id.tv_username)
        val streakView = findViewById<TextView>(R.id.tv_streak)

        // Benutzer-ID aus SharedPreferences laden
        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val userId = prefs.getString("USER_ID", null)

        if (userId != null) {
            // Benutzer-Daten laden
            userViewModel.loadInitialData(userId)

            // Änderungen beobachten & UI aktualisieren
            lifecycleScope.launch {
                userViewModel.user.collect { userEntity ->
                    userEntity?.let {
                        nameView.text = it.username
                        streakView.text = it.streak.toString()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Kein Nutzer gefunden!", Toast.LENGTH_SHORT).show()
        }

        // Navigationsleiste einrichten
        setupNavigation()
    }

    // Klick-Listener für untere Navigationsleiste
    private fun setupNavigation() {
        findViewById<ImageView>(R.id.nav_group).setOnClickListener {
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_add).setOnClickListener {
            startActivity(Intent(this, CreateNewGroupActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_settings).setOnClickListener {
            startActivity(Intent(this, ProfileSettingsActivity::class.java))
        }
    }
}
