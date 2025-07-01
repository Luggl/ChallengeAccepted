package de.thws.challengeaccepted
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
        val gridExercises=findViewById<GridLayout>(R.id.grid_exercises)

        //alle Übungen durchgehen
        for (i in 0 until gridExercises.childCount) {
            val child = gridExercises.getChildAt(i) as? LinearLayout ?: continue

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
                }else{
                    //neuAuswählen
                    selectedExercises.add(text)
                    child.setBackgroundResource(R.drawable.blue_frame)
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
            val intent = Intent(this, SurvivalIntensityActivity::class.java)
            intent.putStringArrayListExtra("selectedExercises", ArrayList(selectedExercises))
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
        navProfile.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}