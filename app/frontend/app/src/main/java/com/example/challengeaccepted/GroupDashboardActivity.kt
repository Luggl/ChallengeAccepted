package com.example.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

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

        // Bottom Navigation
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
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Was möchtest du tun?")
            dialogBuilder.setMessage("Wähle eine Aktion:")

            dialogBuilder.setPositiveButton("Aufgabe erledigen") { _, _ ->
                Toast.makeText(this, "Aufgaben-Erledigungs-Flow startet...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RecordActivity::class.java)
                    startActivity(intent)
            }

            dialogBuilder.setNegativeButton("Challenge erstellen") { _, _ ->
                Toast.makeText(this, "Challenge-Erstellung startet...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CreateChallengeModeActivity::class.java)
                    startActivity(intent)
            }

            // Abbrechen
            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            alertDialog.setOnShowListener {
                // Titel & Nachricht in weiß
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)

                // Buttonfarben
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.button_green))
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(getColor(R.color.button_green))
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(Color.LTGRAY)
            }

            alertDialog.show()
        }

        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}