package com.example.challengeaccepted
import android.annotation.SuppressLint
import android.content.Intent
//import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
//import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class CreateChallengeModeActivity : AppCompatActivity() {
    //Modusauswahl (Standard voreingestellt)
    private var selectedMode: String="standard"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        //Randloses Layout aktivieren (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_challenge_mode)

        //Views aus dem XML holen 
        val navBack = findViewById<ImageView>(R.id.btn_back)
        val confirmButton=findViewById<ImageButton>(R.id.btn_confirm_selection)
        val imageStandard=findViewById<ImageView>(R.id.iv_standard)
        val imageSurvival=findViewById<ImageView>(R.id.iv_survival)


        navBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
//        confirmButton.setOnClickListener {
//            val intent = Intent(this, StandardActivitiesActivity::class.java)
//            startActivity(intent)
//        }

        //Standard-Modus visuelle hervorheben beim Start
        imageStandard.setBackgroundResource(R.drawable.green_frame)
        imageSurvival.setBackgroundResource(R.drawable.bright_grey_frame)


        //Manuelle Auswahl - wenn Nutzer etwas anderes auswählt
        imageStandard.setOnClickListener{
            imageStandard.setBackgroundResource(R.drawable.green_frame)
            imageSurvival.setBackgroundResource(R.drawable.bright_grey_frame)
            selectedMode= "standard"
        }
        imageSurvival.setOnClickListener{
            imageSurvival.setBackgroundResource(R.drawable.green_frame)
            imageStandard.setBackgroundResource(R.drawable.bright_grey_frame)
            selectedMode="survival"
        }
        //auswahl bestätigen
        confirmButton.setOnClickListener{
            val intent=when (selectedMode) {
                "standard" -> Intent(this, StandardActivitiesActivity::class.java)
                "survival" -> Intent(this, SurvivalActivitiesActivity::class.java)
                else -> null
            }
            if (intent!=null){
                startActivity(intent)
            }else{
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
}