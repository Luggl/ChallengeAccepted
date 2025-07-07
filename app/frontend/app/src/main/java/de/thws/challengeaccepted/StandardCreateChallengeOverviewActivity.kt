package de.thws.challengeaccepted

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import de.thws.challengeaccepted.models.StandardChallengeRequest
import de.thws.challengeaccepted.models.SportartIntensity
import de.thws.challengeaccepted.ui.viewmodels.CreateChallengeViewModel
import java.text.SimpleDateFormat
import java.util.*

class StandardCreateChallengeOverviewActivity : AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var btnStartDate: Button
    private lateinit var btnEndDate: Button
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var confirmButton: ImageButton

    private var startDate: Calendar? = null
    private var endDate: Calendar? = null

    // ViewModel
    private val createChallengeViewModel: CreateChallengeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_create_challenge_overview)

        tableLayout = findViewById(R.id.table_layout)
        btnStartDate = findViewById(R.id.btn_start_date)
        btnEndDate = findViewById(R.id.btn_end_date)
        tvStartDate = findViewById(R.id.tv_start_date)
        tvEndDate = findViewById(R.id.tv_end_date)
        confirmButton = findViewById(R.id.btn_confirm_selection)

        // Daten aus Intent holen
        @Suppress("DEPRECATION")
        val intensityMap = intent.getSerializableExtra("intensities") as? HashMap<String, Pair<Int, Int>>
        val incomingGroupId = intent.getStringExtra("groupId") ?: ""
        if (intensityMap == null || incomingGroupId.isBlank()) {
            Toast.makeText(this, "Fehler: Es fehlen Daten!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // --- HIER Tabelle anzeigen ---
        fillTable(intensityMap)

        // DatePicker für Startdatum
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
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        }

        // DatePicker für Enddatum
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
            datePicker.datePicker.minDate = startDate!!.timeInMillis
            datePicker.show()
        }

        // Sportarten Mapping
        val sportartNameToId = mapOf(
            "Push-Ups" to "5cf913b2-d0b8-543f-bf61-ba209f6c0fad",
            "Sit-Ups" to "7e042c26-b6e6-5420-b0c8-d2e543108976",
            "Squats" to "67727a0a-3bb5-5f6a-9bba-7a12c4adf4cc",
            "Lunges" to "a31eece7-3623-5ae6-ba43-3210fe706e32",
            "Burpees" to "2c73675b-996c-5020-a0ef-e17dda08b9fc",
            "Plank" to "0e9dcf37-eb51-548d-8480-4fbe9bf0aef4"
        )

        // Challenge abschicken
        confirmButton.setOnClickListener {
            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Bitte Start- und Enddatum wählen.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sportarten = intensityMap.mapNotNull { (name, pair) ->
                sportartNameToId[name]?.let { id ->
                    SportartIntensity(
                        sportart_id = id,
                        startintensität = pair.first,
                        zielintensität = pair.second
                    )
                }
            }

            if (sportarten.isEmpty()) {
                Toast.makeText(this, "Bitte mindestens eine Sportart auswählen!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDateIso = formatIsoDate(startDate!!)
            val endDateIso = formatIsoDate(endDate!!)

            val request = StandardChallengeRequest(
                startdatum = startDateIso,
                enddatum = endDateIso,
                sportarten = sportarten
            )

            createChallengeViewModel.createChallenge(incomingGroupId, request)
        }

        // Response beobachten
        createChallengeViewModel.challengeResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(this, "Challenge erfolgreich erstellt!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, GroupDashboardActivity::class.java)
                    intent.putExtra("GROUP_ID", incomingGroupId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Fehler: ${it.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun fillTable(data: Map<String, Pair<Int, Int>>) {
        // Vorher aufräumen, falls schon Zeilen da sind (außer Header!)
        if (tableLayout.childCount > 1) {
            tableLayout.removeViews(1, tableLayout.childCount - 1)
        }

        val sportartOrder = listOf(
            "Push-Ups", "Sit-Ups", "Lunges", "Plank", "Squats", "Burpees"
        )

        // KORREKTE TYPISIERUNG!
        val sortedData: List<Pair<String, Pair<Int, Int>>> =
            sportartOrder.mapNotNull { name ->
                data[name]?.let { pair -> name to pair }
            } + data.filterKeys { it !in sportartOrder }.map { it.key to it.value }

        for ((exercise, pair) in sortedData) {
            val (start, end) = pair

            // Nur anzeigen, wenn Werte gesetzt wurden
            if (start == 0 && end == 0) continue

            val color = android.graphics.Color.WHITE // Design anpassen!
            val startText = if (exercise == "Plank") formatSeconds(start) else "$start"
            val endText = if (exercise == "Plank") formatSeconds(end) else "$end"

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
        return String.format("%d:%02d", minutes, secs)
    }

    private fun formatDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun formatIsoDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}
