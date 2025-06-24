package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class StandardCreateChallengeOverviewActivity: AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standard_create_challenge_overview)

        tableLayout = findViewById(R.id.table_layout)

        val intensityMap =
            intent.getSerializableExtra("intensities") as? HashMap<String, Pair<Int, Int>> ?: hashMapOf()
        fillTable(intensityMap)
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
}