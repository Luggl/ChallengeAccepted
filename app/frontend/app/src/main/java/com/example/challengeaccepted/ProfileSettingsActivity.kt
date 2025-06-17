package com.example.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ProfileSettingsActivity :  AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        // Bottom Navigation Icons initialisierenj
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navAdd = findViewById<ImageView>(R.id.nav_add)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        val navBack = findViewById<ImageView>(R.id.btn_back)
        val changePass = findViewById<Button>(R.id.btn_change_password)
        val datenschutz = findViewById<Button>(R.id.btn_datenschutz)
        val logout = findViewById<Button>(R.id.btn_logout)
        val deaktiv_delete_prof = findViewById<Button>(R.id.btn_deactivate_delete)

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

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        navBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        changePass.setOnClickListener {
            val intent = Intent(this, ProfileChangePassActivity::class.java)
            startActivity(intent)
        }

        changePass.setOnClickListener {
            val intent = Intent(this, ProfileChangePassActivity::class.java)
            startActivity(intent)
        }
    }
}
