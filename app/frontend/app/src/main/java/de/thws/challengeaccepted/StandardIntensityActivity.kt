package de.thws.challengeaccepted
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat


class StandardIntensityActivity : AppCompatActivity() {
    //Views
    private lateinit var tvExerciseName: TextView
    private lateinit var ivExerciseIcon: ImageView
    private lateinit var seekbarStart: SeekBar
    private lateinit var seekbarEnde: SeekBar
    private lateinit var tvStartValue: TextView
    private lateinit var tvEndValue: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnConfirm: ImageButton

    // Aktueller Zustand
    private var currentIndex = 0
    private lateinit var exercises: List<String>
    private val intensityMap = mutableMapOf<String, Pair<Int, Int>>()

    // Icon-Zuweisung
    private val exerciseIcons = mapOf(
        "Push-Ups" to R.drawable.pushups_icon,
        "Sit-Ups" to R.drawable.situps_icon,
        "Lunges" to R.drawable.lunges_icon,
        "Planks" to R.drawable.plank_icon,
        "Squats" to R.drawable.squat_icon,
        "Burpees" to R.drawable.burpees_icon
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        //verknüpft diese Activity mit dem XML Layout
        setContentView(R.layout.activity_standard_intensity)

        //referenzieren
        tvExerciseName = findViewById(R.id.tv_standard)
        ivExerciseIcon = findViewById(R.id.iv_exercise_icon)
        seekbarStart = findViewById(R.id.seekbar_start)
        seekbarEnde = findViewById(R.id.seekbar_end)
        tvStartValue = findViewById(R.id.tv_start_main)
        tvEndValue = findViewById(R.id.tv_end_main)
        btnBack = findViewById(R.id.btn_back)
        btnConfirm = findViewById(R.id.btn_confirm_selection)


        //Übungen laden
        exercises = intent.getStringArrayListExtra("selectedExercises") ?: listOf()
        if (exercises.isEmpty()) {
            Toast.makeText(this, "Keine Übungen übergeben", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        //Startintensität Listener
        seekbarStart.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val endValue = seekbarEnde.progress
                val valid = if (progress > endValue) endValue else progress
                seekbarStart.progress = valid
                tvStartValue.text = valid.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

// SeekBar-Listener END
        seekbarEnde.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val startValue = seekbarStart.progress
                val valid = if (progress < startValue) startValue else progress
                seekbarEnde.progress = valid
                tvEndValue.text = valid.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        //Erste Übung anzeigen
        showExercise()

        //Zurück Button
        btnBack.setOnClickListener {
            if (currentIndex == 0) {
                finish() //zurück zur Auswahlleiste
            } else {
                currentIndex--
                showExercise()
            }
        }

        //weiter Button
        btnConfirm.setOnClickListener {
            val current = exercises[currentIndex]
            val start = seekbarStart.progress
            val end = seekbarEnde.progress
            intensityMap[current] = start to end

            if (currentIndex < exercises.size - 1) {
                currentIndex++
                showExercise()
            } else {
                Toast.makeText(this, "Fertig! $intensityMap", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, StandardChallengeOverviewActivity::class.java)
                intent.putExtra("intensities", HashMap(intensityMap))
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

    // Zeigt aktuelle Übung
    private fun showExercise() {
        val name = exercises[currentIndex]
        tvExerciseName.text = name
        ivExerciseIcon.setImageResource(exerciseIcons[name] ?: R.drawable.default_icon)
        val values = intensityMap[name] ?: (0 to 0)
        seekbarStart.progress = values.first
        seekbarEnde.progress = values.second
        tvStartValue.text = values.first.toString()
        tvEndValue.text = values.second.toString()
    }
}
