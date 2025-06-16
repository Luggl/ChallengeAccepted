package com.example.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity :  AppCompatActivity()  {

    private lateinit var navGroup: ImageView
    private lateinit var navHome: ImageView
    private lateinit var navAdd: ImageView
    private lateinit var navProfile: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Bottom Navigation Icons initialisieren
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navAdd = findViewById<ImageView>(R.id.nav_add)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        val navSettings = findViewById<ImageView>(R.id.nav_settings)

        // Navigation Click Listener

        navAdd.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

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
            startActivity(intent)*/
        }
    }