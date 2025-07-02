package de.thws.challengeaccepted

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import java.text.SimpleDateFormat
import java.util.*

class StandardCreateChallengeOverviewActivity: AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var btnStartDate: Button
    private lateinit var btnEndDate: Button
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView

    private var startDate: Calendar?= null
    private var endDate: Calendar?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_create_challenge_overview)

        //views Initialisieren
        tableLayout=findViewById(R.id.table_layout)
        btnStartDate=findViewById(R.id.btn_start_date)
        btnEndDate=findViewById(R.id.btn_end_date)
        tvStartDate=findViewById(R.id.tv_start_date)
        tvEndDate=findViewById(R.id.tv_end_date)

        // Startdatum auswählen
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

        // Enddatum auswählen
        btnEndDate.setOnClickListener {
            if (startDate == null) {
                tvEndDate.text = "Bitte zuerst Startdatum wählen!"
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedEnd = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }

                    if (selectedEnd.before(startDate)) {
                        tvEndDate.text = "Enddatum darf nicht vor Startdatum liegen!"
                    } else {
                        endDate = selectedEnd
                        tvEndDate.text = "Ende: ${formatDate(endDate!!)}"
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = startDate!!.timeInMillis // ab Startdatum
            datePicker.show()
        }



        // Navigation Back
        val navBack = findViewById<ImageView>(R.id.btn_back)
        navBack.setOnClickListener {
            val intent = Intent(this, StandardIntensityActivity::class.java)
            //ausgewählte Übungen holen und zurückreichen
            val selectedExercises=intent.getStringArrayListExtra("selectedExercises")
            val intensities=intent.getSerializableExtra("intensities")as? HashMap<String, Pair<Int, Int>>
            intent.putStringArrayListExtra("selectedExercises", ArrayList(selectedExercises ?: listOf()))
            intent.putExtra("intensities", intensities)
            startActivity(intent)
            finish()
        }
        val confirmButton = findViewById<ImageButton>(R.id.btn_confirm_selection)
        confirmButton.setOnClickListener {
            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Bitte Start- und Enddatum wählen.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, StandardChallengeOverviewActivity::class.java)
                intent.putExtra("startDate", formatDate(startDate!!))
                intent.putExtra("endDate", formatDate(endDate!!))
                startActivity(intent)
                finish()
            }
        }

        tableLayout = findViewById(R.id.table_layout)

        val intensityMap =
            intent.getSerializableExtra("intensities") as? HashMap<String, Pair<Int, Int>> ?: hashMapOf()
        fillTable(intensityMap)


        // Button Navigation
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
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
    private fun fillTable(data: Map<String, Pair<Int, Int>>) {
        for ((exercise, pair) in data) {
            val (start, end) = pair

            // Nur anzeigen, wenn echte Werte vorhanden sind
            if (start == 0 && end == 0) continue

            val color = android.graphics.Color.CYAN // Für gewählte Übungen

            val startText = if (exercise == "Planks") formatSeconds(start) else "$start St."
            val endText = if (exercise == "Planks") formatSeconds(end) else "$end St."

            val row = TableRow(this)

            val exerciseView = TextView(this).apply {
                text = exercise
                setTextColor(color)
                setPadding(8, 8, 8, 8)
            }

            val startView = TextView(this).apply {
                text = startText
                setTextColor(color)
                setPadding(8, 8, 8, 8)
            }

            val endView = TextView(this).apply {
                text = endText
                setTextColor(color)
                setPadding(8, 8, 8, 8)
            }

            row.addView(exerciseView)
            row.addView(startView)
            row.addView(endView)

            tableLayout.addView(row)
        }
    }

    private fun formatSeconds(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d Sek.", minutes, secs)
    }
    private fun formatDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}