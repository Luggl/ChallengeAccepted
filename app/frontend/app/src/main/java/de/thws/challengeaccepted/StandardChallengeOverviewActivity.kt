package de.thws.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class StandardChallengeOverviewActivity : AppCompatActivity() {

    // Hilfsfunktion: dp → px (für systemabhängige Abstände)
    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_challenge_overview)

        // Aktiviert Edge-to-Edge-Modus (z. B. Statusleiste transparent)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        // Dynamisches Padding oben für Systemleisten (z. B. Statusleiste)
        ViewCompat.setOnApplyWindowInsetsListener(rootScroll) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        // Padding oben/unten für Bottom Navigation (damit sie nicht verdeckt wird)
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                8.dpToPx(),
                view.paddingRight,
                8.dpToPx(),
            )
            insets
        }

        // Navigation Bar (unten) schwarz einfärben
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // Button: Challenge verlassen – zeigt Bestätigungsdialog
        val leaveBtn = findViewById<Button>(R.id.btn_leave_challenge)
        leaveBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Challenge wirklich verlassen?")
            dialogBuilder.setMessage("Challenge wirklich verlassen?")

            dialogBuilder.setPositiveButton("Challenge verlassen") { _, _ ->
                Toast.makeText(this, "Du hast die Challenge verlassen.", Toast.LENGTH_SHORT).show()
            }

            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund für Dialog
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            // Farben anpassen, sobald Dialog sichtbar wird
            alertDialog.setOnShowListener {
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)

                val messageView = alertDialog.findViewById<TextView>(android.R.id.message)
                messageView?.setTextColor(Color.WHITE)

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.button_red))
            }

            alertDialog.show()
        }

        // Bottom Navigation: Gruppenübersicht öffnen
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation: Dashboard/Home öffnen
        val navHome = findViewById<ImageView>(R.id.nav_home)
        navHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation: Öffnet Dialog zur Aktionsauswahl
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

            dialogBuilder.setNeutralButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund für Dialogfenster
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            // Farben setzen, sobald Dialog angezeigt wird
            alertDialog.setOnShowListener {
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.button_green))
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(getColor(R.color.button_green))
            }

            alertDialog.show()
        }

        // Bottom Navigation: Profilseite öffnen
        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
