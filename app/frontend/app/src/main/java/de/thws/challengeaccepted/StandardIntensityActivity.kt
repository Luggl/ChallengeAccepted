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
import com.example.challengeaccepted.R

data class ExerciseIntensity(
    val name:String,
    val iconResId: Int,
    var start: Int=0,
    var end: Int=0
)

class StandardIntensityActivity : AppCompatActivity() {
    // Zentrale Icon-Zuweisung
    private val exerciseIcons = mutableListOf(
        ExerciseIntensity("Push-Ups", R.drawable.pushups_icon),
        ExerciseIntensity("Sit-Ups", R.drawable.situps_icon),
        ExerciseIntensity("Lunges", R.drawable.lunges_icon),
        ExerciseIntensity("Planks", R.drawable.plank_icon),
        ExerciseIntensity("Squats", R.drawable.squat_icon),
        ExerciseIntensity("Burpees", R.drawable.burpees_icon),
    )
    private var currentIndex=0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //verknüpft diese Activity mit dem XML Layout
        setContentView(R.layout.activity_standard_intensity)

        //Views
        val btnBack=findViewById<ImageButton>(R.id.btn_back)
        val btnConfirm=findViewById<ImageButton>(R.id.btn_confirm_selection)
        val tvStart=findViewById<TextView>(R.id.tv_start_main)
        val tvEnd=findViewById<TextView>(R.id.tv_end_main)
        //seekbars
        val seekbarStart=findViewById<SeekBar>(R.id.seekbar_start)
        val seekbarEnd= findViewById<SeekBar>(R.id.seekbar_end)

        //Startintensität Listener
        seekbarStart.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val endValue = seekbarEnd.progress
                val valid=if (progress>endValue)endValue else progress
                seekbarStart.progress=valid
                tvStart.text=valid.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

// SeekBar-Listener END
        seekbarEnd.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val startValue = seekbarStart.progress
                val valid = if (progress < startValue) startValue else progress
                seekbarEnd.progress = valid
                tvEnd.text = valid.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //Button bestätigt Werte & lädt nächste Übung
        btnConfirm.setOnClickListener{
            val current=exerciseIcons[currentIndex]
            current.start=seekbarStart.progress
            current.end=seekbarEnd.progress

            if (currentIndex<exerciseIcons.size-1){
                currentIndex++
                loadExercise(exerciseIcons[currentIndex])
            }else{
                //alle Übungen abgeschlossen
                Toast.makeText(this, "Fertig! ${exerciseIcons.size} Übungen gespeichert.", Toast.LENGTH_LONG).show()
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
        navProfile.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
    private fun loadExercise(exercise: ExerciseIntensity) {
        val title = findViewById<TextView>(R.id.tv_standard)
        val icon = findViewById<ImageView>(R.id.iv_exercise_icon)
        val tvStart = findViewById<TextView>(R.id.tv_start_main)
        val tvEnd = findViewById<TextView>(R.id.tv_end_main)
        val seekbarStart = findViewById<SeekBar>(R.id.seekbar_start)
        val seekbarEnd = findViewById<SeekBar>(R.id.seekbar_end)

        title.text = exercise.name
        icon.setImageResource(exercise.iconResId)
        seekbarStart.progress = exercise.start
        seekbarEnd.progress = exercise.end
        tvStart.text = exercise.start.toString()
        tvEnd.text = exercise.end.toString()
    }
}
