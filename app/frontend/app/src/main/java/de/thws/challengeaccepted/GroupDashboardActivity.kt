package de.thws.challengeaccepted

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

// Für Bilder von URLs (optional, falls du ein Bild hast und Glide nutzen willst)
import com.bumptech.glide.Glide

class GroupDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_dashboard)

        // Übergebene Daten holen
        val groupId = intent.getStringExtra("GROUP_ID")
        val groupName = intent.getStringExtra("GROUP_NAME")
        val groupBeschreibung = intent.getStringExtra("GROUP_BESCHREIBUNG")
        val groupBild = intent.getStringExtra("GROUP_BILD")

        // Gruppenname setzen (passt die ID ggf. an dein Layout an)
        val groupNameTextView = findViewById<TextView>(R.id.tvGroupNameDashboard)
        groupNameTextView?.text = groupName ?: "Kein Name"


        // Gruppenbild anzeigen (optional)
        val groupImageView = findViewById<ImageView>(R.id.ivGroupImageDashboard)
        if (!groupBild.isNullOrEmpty() && groupImageView != null) {
            // Falls Bild eine URL ist, lade es mit Glide (füge Glide als Dependency hinzu!)
            Glide.with(this).load(groupBild).into(groupImageView)
        } else {
            // Setze ein Platzhalter-Bild, falls kein Bild vorhanden
            groupImageView?.setImageResource(R.drawable.group_profile_picture)
        }

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

            val alertDialog = dialogBuilder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            alertDialog.setOnShowListener {
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(getColor(R.color.white))
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(getColor(R.color.white))
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.white))
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(getColor(R.color.white))
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
