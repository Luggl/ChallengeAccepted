package de.thws.challengeaccepted

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import de.thws.challengeaccepted.data.database.AppDatabase
import de.thws.challengeaccepted.data.repository.ChallengeRepository
import de.thws.challengeaccepted.models.SurvivalChallengeRequest
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.ui.viewmodels.CreateChallengeViewModel
import de.thws.challengeaccepted.ui.viewmodels.CreateChallengeViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class SurvivalCreateChallengeOverviewActivity : AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var btnStartDate: Button
    private lateinit var tvStartDate: TextView
    private lateinit var confirmButton: ImageButton

    private var startDate: Calendar? = null

    private val createChallengeViewModel: CreateChallengeViewModel by viewModels{
        val db = AppDatabase.getDatabase(applicationContext)
        val service = ApiClient.getRetrofit(applicationContext).create(de.thws.challengeaccepted.network.ChallengeService::class.java)
        val repo = ChallengeRepository(
            service,
            db.challengeDao(),
            db.aufgabeDao()
        )
        CreateChallengeViewModelFactory(repo)
    }
    private lateinit var intensityMap: HashMap<String, String>
    private var groupId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survival_create_challenge_overview)

        // groupId aus Intent holen
        groupId = intent.getStringExtra("groupId") ?: ""

        btnStartDate = findViewById(R.id.btn_start_date)
        tvStartDate = findViewById(R.id.tv_start_date)
        confirmButton = findViewById(R.id.btn_confirm_selection)
        tableLayout = findViewById(R.id.table_layout)

        // Intensities aus Intent holen
        @Suppress("UNCHECKED_CAST")
        intensityMap = intent.getSerializableExtra("intensities") as? HashMap<String, String> ?: hashMapOf()

        fillTableNeutral(intensityMap)

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
            datePicker.datePicker.minDate = System.currentTimeMillis() // heute oder später
            datePicker.show()
        }

        // Back-Button (zurück zur Intensity, groupId mitgeben!)
        val navBack = findViewById<ImageView>(R.id.btn_back)
        navBack.setOnClickListener {
            val intent = Intent(this, SurvivalIntensityActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putStringArrayListExtra("selectedExercises", ArrayList(intensityMap.keys))
            intent.putExtra("intensities", intensityMap)
            startActivity(intent)
            finish()
        }

        // --- Challenge wirklich erstellen ---
        confirmButton.setOnClickListener {
            if (startDate == null) {
                Toast.makeText(this, "Bitte wähle ein Startdatum aus.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (groupId.isBlank()) {
                Toast.makeText(this, "Fehler: groupId fehlt!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- Sportarten-IDs zuweisen ---
            val sportartNameToId = mapOf(
                "Push-Ups" to "5cf913b2-d0b8-543f-bf61-ba209f6c0fad",
                "Sit-Ups" to "7e042c26-b6e6-5420-b0c8-d2e543108976",
                "Squats" to "67727a0a-3bb5-5f6a-9bba-7a12c4adf4cc",
                "Lunges" to "a31eece7-3623-5ae6-ba43-3210fe706e32",
                "Burpees" to "2c73675b-996c-5020-a0ef-e17dda08b9fc",
                "Planks" to "0e9dcf37-eb51-548d-8480-4fbe9bf0aef4"
            )

            // --- SurvivalChallengeRequest bauen ---
            val sportartenList = intensityMap.mapNotNull { (name, schwierigkeitsgrad) ->
                sportartNameToId[name]?.let { id ->
                    de.thws.challengeaccepted.models.SurvivalSportart(
                        sportart_id = id,
                        schwierigkeitsgrad = schwierigkeitsgrad
                    )
                }
            }

            if (sportartenList.isEmpty()) {
                Toast.makeText(this, "Keine Übungen ausgewählt!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = SurvivalChallengeRequest(
                startdatum = formatIsoDate(startDate!!),
                sportarten = sportartenList
            )

            // --- API Call ---
            createChallengeViewModel.createSurvivalChallenge(groupId, request)
        }

        // --- Beobachten, ob Challenge erfolgreich erstellt ---
        createChallengeViewModel.challengeResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(this, "Survival Challenge erfolgreich erstellt!", Toast.LENGTH_LONG).show()
                    // Nach Erfolg zurück ins GroupDashboard
                    val intent = Intent(this, GroupDashboardActivity::class.java)
                    intent.putExtra("GROUP_ID", groupId)
                    // Wenn du weitere Daten brauchst (z.B. Name, Bild), Intent ergänzen!
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Fehler: ${it.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // --- Bottom Navigation ---
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

        val navHome = findViewById<ImageView>(R.id.nav_home)
        navHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }
    }

    private fun fillTableNeutral(data: Map<String, String>) {
        // Leeren, damit keine Duplikate entstehen!
        if (tableLayout.childCount > 1) {
            tableLayout.removeViews(1, tableLayout.childCount - 1)
        }
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
    private fun formatIsoDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}
