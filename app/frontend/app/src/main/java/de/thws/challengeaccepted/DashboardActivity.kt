package de.thws.challengeaccepted

import android.content.Intent
import android.widget.TextView
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.widget.Toast
import androidx.activity.viewModels
import de.thws.challengeaccepted.ui.viewmodels.UserViewModel

class DashboardActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val tvGreeting = findViewById<TextView>(R.id.tv_greeting)
        val tvStreak = findViewById<TextView>(R.id.tv_streak_count)
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navAdd = findViewById<ImageView>(R.id.nav_add)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)

        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }

        navAdd.setOnClickListener {
            val intent = Intent(this, CreateNewGroupActivity::class.java)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Holt die User-ID, die beim Login mitgegeben wurde
        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val userId = prefs.getString("USER_ID", null)
        if (userId != null) {
            userViewModel.getUser(userId) { user ->
                user?.let {
                    tvGreeting.text = "Hi ${it.username}!"
                    tvStreak.text = it.streak.toString()
                }
            }
        } else {
            Toast.makeText(this, "Kein Nutzer gefunden!", Toast.LENGTH_SHORT).show()
        }
    }
}
