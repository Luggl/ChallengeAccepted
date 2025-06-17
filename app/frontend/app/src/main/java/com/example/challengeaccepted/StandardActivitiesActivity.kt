package com.example.challengeaccepted

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StandardActivitiesActivity : AppCompatActivity() {
    //Liste zur Speicherung der ausgewählten Übungen
    private val selectedExercises = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //xml Layout einbinden
        setContentView(R.layout.activity_standard_activities)

        //Zurück-Button verknüpfen und mit Funktionalität versehen
        val backButton = findViewById<ImageButton>(R.id.btn_back)
        backButton.setOnClickListener {
            //bei klick auf zurück-Button die Aktivität schließen
            finish()
        }
        //Gridlayout einrichten und prüfen, ob es geladen wurde
        val gridExercises = findViewById<GridLayout>(R.id.grid_exercises)

        //Testweise Toast anzeigen bei Klick auf das gesamte Grid
        gridExercises.setOnClickListener{
            Toast.makeText(this, "Grid wurde angeklickt", Toast.LENGTH_SHORT).show()
        }
        //zur Kontrolle Log
        Log.d("DashboardActivity", "Gridlayout geladen: $(gridExercise.childCount} Kinder")

        //einzelene Grid-Elemente klickbar machen
        for (i in 0 until gridExercises.childCount){
            val child=gridExercises.getChildAt(i) as? LinearLayout ?:continue

            //TextView im Kind-Layout finden (der Übungsname)
            val label=child.getChildAt(1)as TextView
            val text=label.text.toString()

            //on click für jede Übung starten
            child.setOnClickListener{
                if (selectedExercises.contains(text)) {
                    //falls bereits ausgewählt: abwählen
                    selectedExercises.remove(text)
                    child.setBackgroundResource(R.drawable.grey_background)
                    Toast.makeText(this, "$text ausgewhlt", Toast.LENGTH_SHORT).show()
                }else{
                    //neu auswählen
                    selectedExercises.add(text)
                    child.setBackgroundResource(R.drawable.green_frame)
                    Toast.makeText(this,"$text ausgewählt", Toast.LENGTH_SHORT).show()
                }

                Log.d("DashboardActivity", "Geklickt: $text")

            }
        }
        //Check-Button einbauen
        val checkButton=findViewById<ImageButton>(R.id.btn_confirm_selection)
        checkButton.setOnClickListener{
            //Ruckmeldung mit Toast+ aktuelle Auswahl ausgeben
            Toast.makeText(this, "Auswahl bestätigt!", Toast.LENGTH_SHORT).show()
            Log.d("DashboardActivity", "Bestätigt: $selectedExercises")
            // Etappe 5: Auswahl an nächste Aktivität übergeben
            val intent = Intent(this, StandardIntenstityActivity::class.java)
            intent.putStringArrayListExtra("selectedExercises", ArrayList(selectedExercises))
            startActivity(intent)
        }
    }
}