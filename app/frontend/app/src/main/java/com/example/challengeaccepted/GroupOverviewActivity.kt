package com.example.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class GroupOverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_overview)

        // Group Dashbord
        val navToGroup = findViewById<LinearLayout>(R.id.group_list)
        navToGroup.setOnClickListener {
            val intent = Intent(this, GroupDashboardActivity::class.java)
            startActivity(intent)
        }


        // Bottom Navigation Icons initialisieren
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navAdd = findViewById<ImageView>(R.id.nav_add)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)

        // Navigation Click Listener
        /*navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }*/

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