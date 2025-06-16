package com.example.challengeaccepted
import android.annotation.SuppressLint
//import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
//import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class CreateChallengeModeActivity : AppCompatActivity() {
    private var selectedMode: String?=null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        //Randloses Layout aktivieren (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //Views aus dem XML holen
        val backButton=findViewById<ImageButton>(R.id.btn_back)
        val confirmButton=findViewById<ImageButton>(R.id.btn_confirm_selection)
        val imageStandard=findViewById<ImageView>(R.id.iv_standard)
        val imageSurvival=findViewById<ImageView>(R.id.iv_survival)

        //Standard-Modus visuelle hervorheben beim Start
        imageStandard.setBackgroundResource(R.drawable.green_frame)
        imageSurvival.setBackgroundResource(R.drawable.bright_grey_frame)


        //zurück-Button: finish activity
        backButton.setOnClickListener{
            finish() //zurück zur letzten Seite
        }

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

        }
    }
}