package com.example.challengeaccepted
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

class SurvivalIntensityActivity : AppCompatActivity() {
    //Aktuell gewählte schwierigkeit
    private var selectedLevel: String="easy"

    //Views
    private lateinit var tvEasy: TextView
    private lateinit var tvMedium: TextView
    private lateinit var tvHard: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnConfirm: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survival_intensity)

        //Views referenzieren
        tvEasy = findViewById(R.id.tv_easy)
        tvMedium = findViewById(R.id.tv_medium)
        tvHard = findViewById(R.id.tv_hard)
        btnBack = findViewById(R.id.btn_back)
        btnConfirm = findViewById(R.id.btn_confirm_selection)

        //Auswahl initial setzen
        updateSelectionUI()

        //klicklistener für die Auswahl
        tvEasy.setOnClickListener {
            selectedLevel = "easy"
            updateSelectionUI()
        }
        tvMedium.setOnClickListener {
            selectedLevel = "medium"
            updateSelectionUI()
        }
        tvHard.setOnClickListener {
            selectedLevel = "hard"
            updateSelectionUI()
        }
        btnBack.setOnClickListener {
            val intent=Intent(this, SurvivalActivitiesActivity::class.java)
            startActivity(intent)
        }
        // Bestätigungs-Button: Auswahl verwenden
        btnConfirm.setOnClickListener {
            val intent = Intent(this, SurvivalChallengeOverviewActivity::class.java)
            intent.putExtra("selectedLevel", selectedLevel)
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

        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
        // UI-Aktualisierung: Rahmen setzen je nach Auswahl
        private fun updateSelectionUI() {
            val selectedDrawable = R.drawable.green_frame
            val defaultDrawable = R.drawable.bright_grey_frame

            tvEasy.setBackgroundResource(if (selectedLevel == "easy") selectedDrawable else defaultDrawable)
            tvMedium.setBackgroundResource(if (selectedLevel == "medium") selectedDrawable else defaultDrawable)
            tvHard.setBackgroundResource(if (selectedLevel == "hard") selectedDrawable else defaultDrawable)
        }
}