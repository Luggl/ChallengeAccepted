package de.thws.challengeaccepted
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import java.text.SimpleDateFormat
import java.util.*

class SurvivalCreateChallengeOverviewActivity: AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var btnStartDate: Button
    private lateinit var tvStartDate: TextView

    private var startDate: Calendar?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survival_create_challenge_overview)

        // Startdatum Views initialisieren
        btnStartDate = findViewById(R.id.btn_start_date)
        tvStartDate = findViewById(R.id.tv_start_date)


        // Startdatum wählen
        btnStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    startDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    tvStartDate.text = "Start: ${formatDate(startDate!!)}"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() //heute oder später
            datePicker.show()
        }

        // Navigation Back
        val navBack = findViewById<ImageView>(R.id.btn_back)
        navBack.setOnClickListener {
            val intent = Intent(this, SurvivalIntensityActivity::class.java)
            //ausgewählte Übungen holen und zurückreichen
            val selectedExercises=intent.getStringArrayListExtra("selectedExercises")
            val intensities=intent.getSerializableExtra("intensities")as? HashMap<String, Pair<Int, Int>>
            intent.putStringArrayListExtra("selectedExercises", ArrayList(selectedExercises ?: listOf()))
            intent.putExtra("intensities", intensities)
            startActivity(intent)
            finish()
        }
        val confirmButton= findViewById<ImageButton>(R.id.btn_confirm_selection)
        confirmButton.setOnClickListener{
            if (startDate == null) {
                Toast.makeText(this, "Bitte wähle ein Startdatum aus.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, SurvivalChallengeOverviewActivity::class.java)
                intent.putExtra("startDate", formatDate(startDate!!))
                startActivity(intent)
                finish()
            }
        }
        tableLayout = findViewById(R.id.table_layout)

        val intesityMap =
            intent.getSerializableExtra("intensities") as? HashMap<String, String> ?: hashMapOf()
        fillTableNeutral(intesityMap)

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
    private fun fillTableNeutral(data: Map<String, String>) {
        data.forEach { (exercise, level) ->
            val row = TableRow(this)

            val exerciseView = TextView(this).apply {
                text = exercise
                setTextColor(resources.getColor(R.color.blue, null))
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }

            val levelView = TextView(this).apply {
                text = level.replaceFirstChar { it.uppercaseChar() }
                setTextColor(resources.getColor(R.color.white, null))
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }

            row.addView(exerciseView)
            row.addView(levelView)
            tableLayout.addView(row)
        }
    }

    private fun formatDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}
