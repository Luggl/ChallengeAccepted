package com.example.challengeaccepted
import android.annotation.SuppressLint
import android.content.Intent
//import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
//import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class CreateChallengeModeActivity : AppCompatActivity() {
    //Modusauswahl (Standard voreingestellt)
    private var selectedMode: String="standard"

    //Views als Properties für späteren Zugriff
    private lateinit var flStandard: FrameLayout
    private lateinit var flSurvival: FrameLayout
    private lateinit var imageStandard: ImageView
    private lateinit var imageSurvival: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        //Randloses Layout aktivieren (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_challenge_mode)

        //Views aus dem XML holen
        flStandard=findViewById(R.id.fl_standard)
        flSurvival=findViewById(R.id.fl_survival)
        imageStandard=findViewById(R.id.iv_standard)
        imageSurvival=findViewById(R.id.iv_survival)


        val navBack = findViewById<ImageView>(R.id.btn_back)
        val confirmButton=findViewById<ImageButton>(R.id.btn_confirm_selection)

        //zurück zur vorherigen Seite
        navBack.setOnClickListener {
            val intent = Intent(this, GroupDashboardActivity::class.java)
            startActivity(intent)
        }

        //Start-Markierung für Standard-Modus setzen, wenn Layout fertig ist
        flStandard.post{
            updateSelectedModeUI()
        }

        //manuelle Auswahl- wenn Nutzer etwas anderes auswählt
        imageStandard.setOnClickListener{
            selectedMode="standard"
            updateSelectedModeUI()

        }
        imageSurvival.setOnClickListener{
            selectedMode="survival"
            updateSelectedModeUI()
        }
        //auswahl bestätigen
        confirmButton.setOnClickListener {
            val intent = when (selectedMode) {
                "standard" -> Intent(this, StandardActivitiesActivity::class.java)
                "survival" -> Intent(this, SurvivalActivitiesActivity::class.java)
                else -> null
            }

            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Ungültiger Modus", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialisieren
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)

        navGroup.setOnClickListener{
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        navHome.setOnClickListener{
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        navProfile.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
    //Funktion zur visuellen Hervorhebung des ausgewählten Modus
    private fun updateSelectedModeUI() {
        if (selectedMode == "standard") {
            imageStandard.setBackgroundResource(R.drawable.green_frame)
            imageSurvival.setBackgroundResource(R.drawable.bright_grey_frame)
        } else {
            imageStandard.setBackgroundResource(R.drawable.bright_grey_frame)
            imageSurvival.setBackgroundResource(R.drawable.green_frame)
        }
    }
}