package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.thws.challengeaccepted.ui.FeedAdapter
import de.thws.challengeaccepted.ui.viewmodels.FeedViewModel
import de.thws.challengeaccepted.ui.viewmodels.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private val feedViewModel: FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvGreeting = findViewById<TextView>(R.id.tv_greeting)
        val tvStreak = findViewById<TextView>(R.id.tv_streak_count)
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navAdd = findViewById<ImageView>(R.id.nav_add)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        val calendarLayout = findViewById<LinearLayout>(R.id.calendar)
        val recyclerView = findViewById<RecyclerView>(R.id.feedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }

        navAdd.setOnClickListener {
            val intent = Intent(this, CreateNewGroupActivity::class.java)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Token aus SharedPreferences holen
        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val token = prefs.getString("token", null)

        if (token != null) {
            feedViewModel.fetchFeed()
            feedViewModel.feed.observe(this) { beitragList ->
                recyclerView.adapter = FeedAdapter(beitragList)
            }
            // Kalender und User laden (Kalender-API verwendet Token!)
            userViewModel.fetchUserAndCalendar()
            // -- USER anzeigen (Name + Streak) --
            userViewModel.user.observe(this) { userObj ->
                if (userObj != null) {
                    tvGreeting.text = "Hi ${userObj.username}!"
                    tvStreak.text = userObj.streak.toString()
                }
            }

            userViewModel.kalender.observe(this) { kalenderMap ->
                calendarLayout.removeAllViews()

                // --- NEU: Nur aktuelle Woche anzeigen ---
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val today = LocalDate.now()
                val weekFields = WeekFields.of(Locale.getDefault())
                val currentWeek = today.get(weekFields.weekOfWeekBasedYear())
                val currentYear = today.year

                // Wochentage Mo–So in dieser Woche bestimmen
                val firstDayOfWeek = today.with(weekFields.dayOfWeek(), 1)
                val weekDates = (0..6).map { firstDayOfWeek.plusDays(it.toLong()) }

                weekDates.forEach { date ->
                    val dateStr = date.format(formatter)
                    val status = kalenderMap[dateStr] ?: "leer"  // <-- Default, wenn nichts gefunden!
                    val tv = TextView(this)
                    tv.text = date.dayOfMonth.toString()
                    tv.gravity = Gravity.CENTER
                    tv.setTextColor(Color.WHITE)
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    val params = LinearLayout.LayoutParams(0, 80, 1f)
                    params.setMargins(6, 0, 6, 0)
                    tv.layoutParams = params

                    // Hintergrund je nach Status
                    when (status) {
                        "abgeschlossen" -> tv.setBackgroundResource(R.drawable.blue_frame)
                        "offen" -> tv.setBackgroundResource(R.drawable.green_frame)
                        "nicht gemacht" -> tv.setBackgroundResource(R.drawable.red_frame)
                        else -> tv.setBackgroundResource(R.drawable.grey_background)  // Neutral
                    }

                    calendarLayout.addView(tv)
                }
            }
        } else {
            Toast.makeText(this, "Kein Token gefunden!", Toast.LENGTH_SHORT).show()
        }
    }
}
