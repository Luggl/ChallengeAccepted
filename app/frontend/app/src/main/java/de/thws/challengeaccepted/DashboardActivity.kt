package de.thws.challengeaccepted

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.thws.challengeaccepted.data.database.AppDatabase
import de.thws.challengeaccepted.data.repository.UserRepository
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.UserService
import de.thws.challengeaccepted.ui.FeedAdapter
import de.thws.challengeaccepted.ui.viewmodels.FeedViewModel
import de.thws.challengeaccepted.ui.viewmodels.UserViewModel
import de.thws.challengeaccepted.ui.viewmodels.UserViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val userService = ApiClient.getRetrofit(applicationContext).create(UserService::class.java)
        val repository = UserRepository(userService, db.userDao())
        UserViewModelFactory(repository)
    }

    private val feedViewModel: FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvGreeting = findViewById<TextView>(R.id.tv_greeting)
        val tvStreak = findViewById<TextView>(R.id.tv_streak_count)
        val calendarLayout = findViewById<LinearLayout>(R.id.calendar)
        val recyclerView = findViewById<RecyclerView>(R.id.feedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val userId = prefs.getString("USER_ID", null)

        // FeedAdapter einmalig erstellen und als Variable halten!
        val feedAdapter = FeedAdapter(emptyList()) { beitragId, vote ->
            // Voting-Callback: Hier das ViewModel aufrufen!
            feedViewModel.vote(beitragId, vote)
        }
        recyclerView.adapter = feedAdapter

        if (token != null && userId != null) {
            // Feed laden
            feedViewModel.fetchFeed()

            // Feed-Observer: Die Feed-Daten werden an den Adapter übergeben
            feedViewModel.feed.observe(this) { beitragList ->
                // Adapter bekommt neue Liste
                feedAdapter.submitList(beitragList)
            }

            // ViewModel mit der User-ID initialisieren
            userViewModel.loadInitialData(userId)

            // User-Daten beobachten und anzeigen
            lifecycleScope.launch {
                userViewModel.user.collect { userEntity ->
                    if (userEntity != null) {
                        tvGreeting.text = "Hi ${userEntity.username}!"
                        tvStreak.text = userEntity.streak.toString()
                    }
                }
            }

            // Kalender beobachten und zeichnen
            userViewModel.kalender.observe(this) { kalenderMap ->
                calendarLayout.removeAllViews()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val today = LocalDate.now()
                val weekFields = WeekFields.of(Locale.getDefault())
                val firstDayOfWeek = today.with(weekFields.dayOfWeek(), 1)
                val weekDates = (0..6).map { firstDayOfWeek.plusDays(it.toLong()) }

                weekDates.forEach { date ->
                    val dateStr = date.format(formatter)
                    val status = kalenderMap[dateStr] ?: "leer"
                    val tv = TextView(this)
                    tv.text = date.dayOfMonth.toString()
                    tv.gravity = Gravity.CENTER
                    tv.setTextColor(Color.WHITE)
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    val params = LinearLayout.LayoutParams(0, 80, 1f)
                    params.setMargins(6, 0, 6, 0)
                    tv.layoutParams = params

                    when (status) {
                        "abgeschlossen" -> tv.setBackgroundResource(R.drawable.blue_frame)
                        "offen" -> tv.setBackgroundResource(R.drawable.green_frame)
                        "nicht gemacht" -> tv.setBackgroundResource(R.drawable.red_frame)
                        else -> tv.setBackgroundResource(R.drawable.grey_background)
                    }
                    calendarLayout.addView(tv)
                }
            }
        } else {
            Toast.makeText(this, "Kein Token oder User-ID gefunden!", Toast.LENGTH_SHORT).show()
            // Optional: Zum Login weiterleiten
        }

        // Navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.nav_group).setOnClickListener {
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_add).setOnClickListener {
            startActivity(Intent(this, CreateNewGroupActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}