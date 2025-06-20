package com.example.challengeaccepted
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SurvivalActivitiesActivity : AppCompatActivity(){
    //Liste zur Speicherung der ausgewählten Übungen
    private val selectedExercises = mutableSetOf<String>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //verknüpft die Activity mit dem zugehörigen XML Layout
        setContentView(R.layout.activity_survival_activities)

        //zurück button
        val backButton=findViewById<ImageButton>(R.id.btn_back)

        //klicklistener: schließt die aktuelle Activity
        backButton.setOnClickListener{
            val intent = Intent(this, CreateChallengeModeActivity::class.java)
            startActivity(intent)
        }
        //Gridlayout mit Übungen
        val gridLayout=findViewById<GridLayout>(R.id.grid_exercises)

        //alle Übungen durchgehen
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i) as? LinearLayout ?: continue

            //Textview im Kind-Layout finden (der Übungsname)
            val label = child.getChildAt(1) as TextView
            val text = label.text.toString()

            //on click für jede Übung starten
            child.setOnClickListener {
                if (selectedExercises.contains(text)) {
                    //falls bereit ausgewählt: abwählen
                    selectedExercises.remove(text)
                    child.setBackgroundResource(R.drawable.bright_grey_frame)
                    Toast.makeText(this, "$text ausgewählt", Toast.LENGTH_SHORT).show()
                }
                Log.d("DashboardActivity", "Geklickt: $text")
            }
        }
        //check button einbauen
        val checkButton=findViewById<ImageButton>(R.id.btn_confirm_selection)
        checkButton.setOnClickListener{
            //Rückmeldung mit Toast+ aktuelle Auswahl ausgeben
            Toast.makeText(this, "Auswahl bestätigt!", Toast.LENGTH_SHORT).show()
            Log.d("DashboardActivity", "Bestätigt: $selectedExercises")
            // Etappe 5: Auswahl an nächste Aktivität übergeben
            val intent = Intent(this, StandardIntensityActivity::class.java)
            intent.putStringArrayListExtra("selectedExercises", ArrayList(selectedExercises))
            startActivity(intent)
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