package com.example.challengeaccepted
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class SurvivalIntensityActivity : AppCompatActivity() {

    //Views
    private lateinit var tvExerciseName: TextView
    private lateinit var ivExerciseIcon: ImageView
    private lateinit var tvEasy: TextView
    private lateinit var tvMedium: TextView
    private lateinit var tvHard: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnConfirm: ImageButton

    //Aktueller Zustand
    private var selectedLevel: String="easy"
    private var currentIndex=0
    private lateinit var exercises: List<String>
    private val intensityMap= mutableMapOf<String, String>()

    // Zentrale Icon-Zuweisung
    private val exerciseIcons = mapOf(
        "Push-Ups" to R.drawable.pushups_icon,
        "Sit-Ups" to R.drawable.situps_icon,
        "Lunges" to R.drawable.lunges_icon,
        "Planks" to R.drawable.plank_icon,
        "Squats" to R.drawable.squat_icon,
        "Burpees" to R.drawable.burpees_icon

    )


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survival_intensity)

        //Views referenzieren
        tvExerciseName=findViewById(R.id.tv_exercise_name)
        ivExerciseIcon=findViewById(R.id.iv_exercise_icon)
        tvEasy = findViewById(R.id.tv_easy)
        tvMedium = findViewById(R.id.tv_medium)
        tvHard = findViewById(R.id.tv_hard)
        btnBack = findViewById(R.id.btn_back)
        btnConfirm = findViewById(R.id.btn_confirm_selection)

        //Auswahl initial setzen
        //updateSelectionUI()

        //ausgewählte Übungen aus vorherigen Seite holen
        exercises=intent.getStringArrayListExtra("selectedExercises")?: arrayListOf()
        if (exercises.isEmpty()){
            Toast.makeText(this,"Keine Übungen ausgewählt",Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        //Erste Übung anzeigen
        showExercise()
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
            if (currentIndex == 0) {
                val intent = Intent(this, SurvivalActivitiesActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                currentIndex--
                selectedLevel = intensityMap[exercises[currentIndex]] ?: "easy"
                showExercise()
                updateSelectionUI()
            }
        }
        // Bestätigungs-Button: Auswahl verwenden
        btnConfirm.setOnClickListener {
            val currentExercise=exercises[currentIndex]
            intensityMap[currentExercise]=selectedLevel

            if (currentIndex<exercises.size-1){
                currentIndex++
                selectedLevel="easy"
                showExercise()
                updateSelectionUI()
            }else{
                //alle Übungen abgeschlossen -> weitergeben
                Toast.makeText(this, "Fertig! Ausgewählt: $intensityMap", Toast.LENGTH_SHORT).show()
                val intent=Intent(this, SurvivalChallengeOverviewActivity::class.java)
                intent.putExtra("intesities", HashMap(intensityMap))
                startActivity(intent)
                finish()
            }
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
    private fun showExercise(){
        val exercise=exercises[currentIndex]
        tvExerciseName.text=exercise
        ivExerciseIcon.setImageResource(exerciseIcons[exercise]?: R.drawable.default_icon)
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