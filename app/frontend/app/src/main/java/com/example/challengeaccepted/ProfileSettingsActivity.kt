package com.example.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileSettingsActivity :  AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)


        // Navigation Back
        val navBack = findViewById<ImageView>(R.id.btn_back)

        navBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
//
//        // Name Bearbeiten
//        val changeName = findViewById<ImageView>(R.id.change)
//        val editName = findViewById<ImageView>(R.id.et_name)
//
//        editName.setOnClickListener {
//            editName.isEnabled = true
//            editName.requestFocus()
//           // editName.setSelection(editName.text.length) // Cursor ans Ende setzen
//        }

        // Navigation Passwort ändern
        val navChangePass = findViewById<Button>(R.id.btn_change_password)

//        navChangePass.setOnClickListener {
//            val intent = Intent(this, ProfileChangePassActivity::class.java)
//            startActivity(intent)
//        }

        navChangePass.setOnClickListener {
        Toast.makeText(this, "Funktioniert!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

        // Bottom Navigation Icons initialisieren
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navAdd = findViewById<ImageView>(R.id.nav_add)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)

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
    }
}
