package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import de.thws.challengeaccepted.ui.viewmodels.UserViewModel

class ProfileActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Hole die Views für dynamische Daten
        val nameView = findViewById<TextView>(R.id.tv_username)
        val streakView = findViewById<TextView>(R.id.tv_streak)

        // Bottom Navigation (wie gehabt)
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }
        val navHome = findViewById<ImageView>(R.id.nav_home)
        navHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
        val navAdd = findViewById<ImageView>(R.id.nav_add)
        navAdd.setOnClickListener {
            val intent = Intent(this, CreateNewGroupActivity::class.java)
            startActivity(intent)
        }
        val navSet = findViewById<ImageView>(R.id.nav_settings)
        navSet.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

        // USER_ID aus SharedPreferences holen
        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val userId = prefs.getString("USER_ID", null)

        if (userId != null) {
            userViewModel.getUser(userId) { user ->
                user?.let {
                    nameView.text = it.username
                    streakView.text = it.streak.toString()
                }
            }
        } else {
            Toast.makeText(this, "Kein Nutzer gefunden!", Toast.LENGTH_SHORT).show()
        }
    }
}
