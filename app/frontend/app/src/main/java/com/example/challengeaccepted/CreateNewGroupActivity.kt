package com.example.challengeaccepted

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.core.view.WindowCompat

class CreateNewGroupActivity : AppCompatActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    //Randloses Layout aktivieren (Edge-to-Edge)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_create_new_group)

    val inviteBtn = findViewById<Button>(R.id.btn_invite_friends)
    inviteBtn.setOnClickListener {
        val inviteLink = "https://challenge-app.de/invite/ABC123"

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Lade Freunde ein!")
        dialogBuilder.setMessage("Kopiere den Einladungslink:\n\n$inviteLink")

        dialogBuilder.setPositiveButton("Link kopieren") { _, _ ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Einladungslink", inviteLink)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Link kopiert!", Toast.LENGTH_SHORT).show()
        }

        dialogBuilder.setNeutralButton("Abbrechen") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()

        // Hintergrund schwarz
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        alertDialog.setOnShowListener {
            // Textfarbe für Titel und Nachricht
            val titleId = resources.getIdentifier("alertTitle", "id", "android")
            val titleView = alertDialog.findViewById<TextView>(titleId)
            titleView?.setTextColor(Color.WHITE)

            val messageView = alertDialog.findViewById<TextView>(android.R.id.message)
            messageView?.setTextColor(Color.WHITE)

            // Buttonfarben
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.button_green))
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(Color.LTGRAY)
        }

        alertDialog.show()
    }


    // Button Navigation
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

    val navProfile = findViewById<ImageView>(R.id.nav_profile)
    navProfile.setOnClickListener {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}
}