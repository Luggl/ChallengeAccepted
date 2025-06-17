package com.example.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity :  AppCompatActivity()  {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialisieren
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navAdd = findViewById<ImageView>(R.id.nav_add)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        val navSet = findViewById<ImageView>(R.id.nav_settings)

        // Navigation Click Listener
        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }

        navHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        navAdd.setOnClickListener {
            val intent = Intent(this, CreateNewGroupActivity::class.java)
            startActivity(intent)
        }

        /*navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }*/

        //Settings Seite
        navSet.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

/*navSet.setOnClickListener {
    Toast.makeText(this, "Funktioniert!", Toast.LENGTH_SHORT).show()
    val intent = Intent(this, RegisterActivity::class.java)
    startActivity(intent)
}*/
}
}