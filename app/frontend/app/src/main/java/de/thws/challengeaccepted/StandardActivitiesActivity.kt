package de.thws.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class StandardActivitiesActivity : AppCompatActivity() {

    // Liste zur Speicherung der ausgewählten Übungen (als Set, um Duplikate zu vermeiden)
    private val selectedExercises = mutableSetOf<String>()

    // Hilfsfunktion zur Umrechnung von dp in Pixel
    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_activities)

        // Randloses Layout (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Views für Inset-Anpassungen (oben/unten)
        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        // Systemleisten oben (z. B. Notch, Uhrzeit)
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

        // Systemleiste unten (Navigation)
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

        // Farbe der Navigationsleiste unten
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // Zurück-Button → führt zur Moduswahl zurück
        val backButton = findViewById<ImageButton>(R.id.btn_back)
        backButton.setOnClickListener {
            val intent = Intent(this, CreateChallengeModeActivity::class.java)
            startActivity(intent)
        }

        // Grid mit allen Übungen referenzieren
        val gridExercises = findViewById<GridLayout>(R.id.grid_exercises)

        // Gruppen-ID aus vorheriger Seite mitnehmen
        val groupId = intent.getStringExtra("groupId")

        // Durchlaufe alle Grid-Elemente
        for (i in 0 until gridExercises.childCount) {
            val child = gridExercises.getChildAt(i) as? LinearLayout ?: continue

            // TextView mit Übungsname innerhalb des Elements holen
            val label = child.getChildAt(1) as TextView
            val text = label.text.toString()

            // Klick-Logik für das jeweilige Übungselement
            child.setOnClickListener {
                if (selectedExercises.contains(text)) {
                    // Übung war bereits ausgewählt → abwählen
                    selectedExercises.remove(text)
                    child.setBackgroundResource(R.drawable.bright_grey_frame)
                    Toast.makeText(this, "$text ausgewhlt", Toast.LENGTH_SHORT).show()
                } else {
                    // Neue Auswahl
                    selectedExercises.add(text)
                    child.setBackgroundResource(R.drawable.blue_frame)
                    Toast.makeText(this, "$text ausgewählt", Toast.LENGTH_SHORT).show()
                }

                Log.d("DashboardActivity", "Geklickt: $text")
            }
        }

        // Check-Button → navigiert zur Intensitätsauswahl
        val checkButton = findViewById<ImageButton>(R.id.btn_confirm_selection)
        checkButton.setOnClickListener {
            if (selectedExercises.isEmpty()) {
                Toast.makeText(this, "Bitte wähle mindestens eine Übung aus!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Neue Seite öffnen und ausgewählte Übungen mitgeben
            val intent = Intent(this, StandardIntensityActivity::class.java)
            intent.putStringArrayListExtra("selectedExercises", ArrayList(selectedExercises))
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

        // Navigation: Gruppenübersicht
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }

        // Navigation: Startseite
        val navHome = findViewById<ImageView>(R.id.nav_home)
        navHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        // Navigation: Profil
        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
