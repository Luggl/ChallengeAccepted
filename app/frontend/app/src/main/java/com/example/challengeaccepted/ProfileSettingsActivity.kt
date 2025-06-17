package com.example.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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




        val etName = findViewById<EditText>(R.id.et_name)
        val ivEditName = findViewById<ImageView>(R.id.change)

        var isEditing = false

        ivEditName.setOnClickListener {
            if (!isEditing) {
                // Editiermodus aktivieren
                etName.isEnabled = true
                etName.requestFocus()
                etName.setSelection(etName.text.length)
                ivEditName.setImageResource(R.drawable.check_icon) // Icon auf „bestätigen“ ändern
                isEditing = true
            } else {
                // Speichern & Editiermodus verlassen
                etName.isEnabled = false
                ivEditName.setImageResource(R.drawable.change_icon) // Zurück auf Stift
                isEditing = false

                val newName = etName.text.toString()
                Toast.makeText(this, "Name geändert zu: $newName", Toast.LENGTH_SHORT).show()
                // Hier ggf. auch speichern (z. B. in SharedPreferences oder Datenbank)
            }
        }

    }
}
