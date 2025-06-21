package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class DashboardActivity :  AppCompatActivity()  {


        override fun onCreate(savedInstanceState: Bundle?) {
            // Randloses Layout aktivieren
            WindowCompat.setDecorFitsSystemWindows(window, false)

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_dashboard)

            // Initialisieren
            val navGroup = findViewById<ImageView>(R.id.nav_group)
            val navHome = findViewById<ImageView>(R.id.nav_home)
            val navAdd = findViewById<ImageView>(R.id.nav_add)
            val navProfile = findViewById<ImageView>(R.id.nav_profile)

            // Navigation Click Listener
            navGroup.setOnClickListener {
                val intent = Intent(this, GroupOverviewActivity::class.java)
                startActivity(intent)
            }

            /*navHome.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }*/

            navAdd.setOnClickListener {
                val intent = Intent(this, CreateNewGroupActivity::class.java)
                startActivity(intent)
            }

            navProfile.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }