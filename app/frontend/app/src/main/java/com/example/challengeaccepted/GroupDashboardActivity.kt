package com.example.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GroupDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_dashboard)

        // Navigation Groupstatus
        val navGroupstatus = findViewById<LinearLayout>(R.id.ll_groupstatus)
        navGroupstatus.setOnClickListener {
            val intent = Intent(this, GroupstatusActivity::class.java)
            startActivity(intent)
        }

        // Navigation Challenge Overview
        val navChalleOv = findViewById<LinearLayout>(R.id.ll_challenge_overview)
        navChalleOv.setOnClickListener {
            val intent = Intent(this, SurvivalChallengeOverviewActivity::class.java)
            startActivity(intent)
        }

        // Navigation Record Activity
        val navRecordAc = findViewById<TextView>(R.id.tv_remaining_time)
        navRecordAc.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
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