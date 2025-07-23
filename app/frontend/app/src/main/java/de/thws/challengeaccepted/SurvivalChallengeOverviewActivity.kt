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

class SurvivalChallengeOverviewActivity : AppCompatActivity() {

    // Hilfsfunktion zur Umrechnung von dp in px (für dynamisches Padding)
    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survival_challenge_overview)

        // Aktiviert Edge-to-Edge-Layout (z. B. hinter der Statusleiste)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        // Setzt systemabhängiges Padding für ScrollView (oben)
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

        // Fügt Padding für untere Navigation hinzu (oben + unten)
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

        // Setzt Hintergrundfarbe der System-Navigationsleiste (unten)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // Stellt sicher, dass `ll_bottom_navigation` nicht von Systembars überlappt wird
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ll_bottom_navigation)) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                systemBarsInsets.bottom
            )
            insets
        }

        // Öffnet Gruppenstatusseite bei Klick auf Gruppeninfo-Leiste
        val navGroupstatus = findViewById<LinearLayout>(R.id.ll_groupstatus)
        navGroupstatus.setOnClickListener {
            val intent = Intent(this, GroupstatusActivity::class.java)
            startActivity(intent)
        }

        // Öffnet Kamera- bzw. Aufzeichnungsseite bei Klick auf verbleibende Zeit
        val navRecordAc = findViewById<TextView>(R.id.tv_remaining_time)
        navRecordAc.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }

        // Challenge-verlassen-Dialog mit Bestätigung
        val leaveBtn = findViewById<Button>(R.id.btn_leave_challenge)
        leaveBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Challenge wirklich verlassen?")
            dialogBuilder.setMessage("Challenge wirklich verlassen?")

            dialogBuilder.setPositiveButton("Challenge verlassen") { _, _ ->
                Toast.makeText(this, "Du hast die Challenge verlassen.", Toast.LENGTH_SHORT).show()
            }

            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund für Dialogfenster
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            // Text- und Buttonfarben anpassen, sobald Dialog sichtbar ist
            alertDialog.setOnShowListener {
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)

                val messageView = alertDialog.findViewById<TextView>(android.R.id.message)
                messageView?.setTextColor(Color.WHITE)

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.button_red))
            }

            alertDialog.show()
        }

        // Bottom Navigation: Gruppenübersicht
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation: Dashboard/Home
        val navHome = findViewById<ImageView>(R.id.nav_home)
        navHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation: Öffnet Dialog zur Auswahl zwischen Aufgabe erledigen oder neue Challenge erstellen
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

            // Schwarzer Dialoghintergrund
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            // Texte und Buttonfarben dynamisch setzen
            alertDialog.setOnShowListener {
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.button_green))
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(getColor(R.color.button_green))
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
