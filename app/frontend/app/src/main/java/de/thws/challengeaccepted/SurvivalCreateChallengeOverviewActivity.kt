package de.thws.challengeaccepted
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class SurvivalCreateChallengeOverviewActivity: AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survival_create_challenge_overview)

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
}
