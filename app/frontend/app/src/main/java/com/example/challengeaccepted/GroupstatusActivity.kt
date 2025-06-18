package com.example.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

class GroupstatusActivity : AppCompatActivity() {

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupstatus)

        // Navigation Challenge Overview
        val navChalleOv = findViewById<Button>(R.id.challenge_overview)
        navChalleOv.setOnClickListener {
            val intent = Intent(this, SurvivalChallengeOverviewActivity::class.java)
            startActivity(intent)
        }

        // Gruppe verlassen PopUp
        val leaveBtn = findViewById<Button>(R.id.btn_leave_group)
        leaveBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Gruppe wirklich verlassen?")
            dialogBuilder.setMessage("Gruppe wirklich verlassen?")

            dialogBuilder.setPositiveButton("Gruppe verlassen") { _, _ ->
                Toast.makeText(this, "Du hast die Gruppe verlassen.", Toast.LENGTH_SHORT).show()
                // TODO: Logik zum Verlassen der Gruppe hier einfügen
            }

            // Abbrechen
            val alertDialog = dialogBuilder.create()

            // Hintergrund schwarz
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            // Dialog anzeigen & Farben setzen
            alertDialog.setOnShowListener {
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)

                val messageView = alertDialog.findViewById<TextView>(android.R.id.message)
                messageView?.setTextColor(Color.WHITE)

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.button_red))
            }

            alertDialog.show()
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

