package de.thws.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class StandardIntensityActivity : AppCompatActivity() {
    // Views
    private lateinit var tvExerciseName: TextView
    private lateinit var ivExerciseIcon: ImageView
    private lateinit var seekbarStart: SeekBar
    private lateinit var seekbarEnde: SeekBar
    private lateinit var tvStartValue: TextView
    private lateinit var tvEndValue: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnConfirm: ImageButton

    // Zustand
    private var currentIndex = 0
    private lateinit var exercises: List<String>
    private val intensityMap = mutableMapOf<String, Pair<Int, Int>>()

    private val exerciseIcons = mapOf(
        "Push-Ups" to R.drawable.pushups_icon,
        "Sit-Ups" to R.drawable.situps_icon,
        "Lunges" to R.drawable.lunges_icon,
        "Planks" to R.drawable.plank_icon,
        "Squats" to R.drawable.squat_icon,
        "Burpees" to R.drawable.burpees_icon
    )

    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_intensity)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        ViewCompat.setOnApplyWindowInsetsListener(rootScroll) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                view.paddingLeft,
                8.dpToPx(),
                view.paddingRight,
                8.dpToPx(),
            )
            insets
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)


        // Views
        tvExerciseName = findViewById(R.id.tv_title)
        ivExerciseIcon = findViewById(R.id.iv_exercise_icon)
        seekbarStart = findViewById(R.id.seekbar_start)
        seekbarEnde = findViewById(R.id.seekbar_end)
        tvStartValue = findViewById(R.id.tv_start_main)
        tvEndValue = findViewById(R.id.tv_end_main)
        btnBack = findViewById(R.id.btn_back)
        btnConfirm = findViewById(R.id.btn_confirm_selection)
        val groupId = intent.getStringExtra("groupId") ?: ""

        // Logging für Debug
        val receivedExercises = intent.getStringArrayListExtra("selectedExercises")
        Log.d("StandardIntensity", "receivedExercises: $receivedExercises")

        exercises = receivedExercises ?: listOf()
        if (exercises.isEmpty()) {
            Toast.makeText(this, "Keine Übungen übergeben", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        seekbarEnde.isEnabled = false

        seekbarStart.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTextView(tvStartValue, progress)
                seekbarEnde.isEnabled = progress > 0
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekbarEnde.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val startValue = seekbarStart.progress
                val valid = if (progress < startValue) startValue else progress
                seekbarEnde.progress = valid
                updateTextView(tvEndValue, valid)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        showExercise()

        btnBack.setOnClickListener {
            if (currentIndex == 0) {
                finish()
            } else {
                currentIndex--
                showExercise()
            }
        }

        btnConfirm.setOnClickListener {
            val current = exercises[currentIndex]
            val start = seekbarStart.progress
            val end = seekbarEnde.progress
            intensityMap[current] = start to end

            if (currentIndex < exercises.size - 1) {
                currentIndex++
                showExercise()
            } else {
                // Log zum Testen
                Log.d("StandardIntensity", "intensityMap: $intensityMap")
                Toast.makeText(this, "Fertig! $intensityMap", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, StandardCreateChallengeOverviewActivity::class.java)
                intent.putExtra("intensities", HashMap(intensityMap))
                intent.putExtra("groupId", groupId)
                startActivity(intent)
                finish()
            }
        }

        // Bottom Navigation
        findViewById<ImageView>(R.id.nav_group).setOnClickListener {
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun showExercise() {
        val name = exercises[currentIndex]
        tvExerciseName.text = name
        ivExerciseIcon.setImageResource(exerciseIcons[name] ?: R.drawable.default_icon)

        // Max-Werte setzen je nach Übung
        if (name == "Planks") {
            seekbarStart.max = 300 // 5 Minuten
            seekbarEnde.max = 600 // 10 Minuten
        } else {
            seekbarStart.max = 50
            seekbarEnde.max = 100
        }

        val values = intensityMap[name] ?: (0 to 0)
        seekbarStart.progress = values.first
        seekbarEnde.progress = values.second

        updateTextView(tvStartValue, values.first)
        updateTextView(tvEndValue, values.second)
    }

    private fun updateTextView(textView: TextView, value: Int) {
        val isPlank = exercises[currentIndex] == "Planks"
        textView.text = if (isPlank) formatSeconds(value) else value.toString()
    }

    private fun formatSeconds(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }
}
