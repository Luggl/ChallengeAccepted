package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
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

    private val userViewModel: UserViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val userService = ApiClient.getRetrofit(applicationContext).create(UserService::class.java)
        val repository = UserRepository(userService, db.userDao())
        // GEÄNDERT: Übergib hier nur noch das Repository
        UserViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val nameView = findViewById<TextView>(R.id.tv_username)
        val streakView = findViewById<TextView>(R.id.tv_streak)

        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val userId = prefs.getString("USER_ID", null)

        if (userId != null) {
            userViewModel.loadInitialData(userId)

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

        setupNavigation()
    }

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