package de.thws.challengeaccepted

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.thws.challengeaccepted.data.database.AppDatabase
import de.thws.challengeaccepted.data.repository.GroupRepository
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.GroupService
import de.thws.challengeaccepted.ui.GroupFeedAdapter
import de.thws.challengeaccepted.ui.viewmodels.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import android.content.res.Resources
import android.util.Log
import de.thws.challengeaccepted.data.entities.Aufgabe
import kotlinx.coroutines.flow.collectLatest

class GroupDashboardActivity : AppCompatActivity() {

    // GroupViewModel mit vollständiger Repository-Initialisierung inkl. DAOs & Context
    private val groupViewModel: GroupViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val service = ApiClient.getRetrofit(applicationContext).create(GroupService::class.java)
        val repository = GroupRepository(
            service,
            applicationContext,
            db.gruppeDao(),
            db.challengeDao(),
            db.beitragDao(),
            db.membershipDao(),
            db.userDao()
        )
        GroupViewModelFactory(repository)
    }

    private lateinit var groupFeedAdapter: GroupFeedAdapter

    // Extension: DP zu PX
    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    // Intent-Erstellung für RecordActivity mit Aufgabeninformationen
    private fun createRecordIntent(aufgabe: Aufgabe, groupId: String, groupName: String): Intent {
        Log.d("GroupDashboardActivity", "Erfuellungs_ID: ${aufgabe.erfuellungId}")
        return Intent(this, RecordActivity::class.java).apply {
            putExtra("TASK_ID", aufgabe.aufgabeId)
            putExtra("TASK_DESC", aufgabe.beschreibung)
            putExtra("GROUP_ID", groupId)
            putExtra("GROUP_NAME", groupName)
            putExtra("Erfuellungs_ID", aufgabe.erfuellungId)
        }
    }

    // Dialog zur Auswahl zwischen Aufgabenerfüllung und Challenge-Erstellung
    private fun showUniversalAddDialog(groupId: String, groupName: String, aufgabe: Aufgabe?) {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Was möchtest du tun?")
            .setMessage("Wähle eine Aktion:")

        if (aufgabe != null) {
            dialogBuilder.setPositiveButton("Aufgabe erledigen") { _, _ ->
                startActivity(createRecordIntent(aufgabe, groupId, groupName))
            }
        }

        dialogBuilder.setNegativeButton("Challenge erstellen") { _, _ ->
            val intent = Intent(this, CreateChallengeModeActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        alertDialog.setOnShowListener {
            val titleId = resources.getIdentifier("alertTitle", "id", "android")
            alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
            alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.WHITE)
        }
        alertDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_dashboard)

        // Edge-to-Edge aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        // System-Insets für Top- und Bottom-Bereich anwenden
        ViewCompat.setOnApplyWindowInsetsListener(rootScroll) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(view.paddingLeft, systemInsets.top, view.paddingRight, view.paddingBottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, _ ->
            view.setPadding(view.paddingLeft, 8.dpToPx(), view.paddingRight, 8.dpToPx())
            WindowInsetsCompat.CONSUMED
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // Gruppen-ID und User-ID prüfen
        val groupId = intent.getStringExtra("GROUP_ID") ?: run {
            Toast.makeText(this, "Fehler: Gruppen-ID fehlt", Toast.LENGTH_LONG).show()
            finish(); return
        }

        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val userId = prefs.getString("USER_ID", null) ?: run {
            Toast.makeText(this, "Fehler: User-ID fehlt", Toast.LENGTH_LONG).show()
            finish(); return
        }

        // View-Zuordnung
        val groupNameTextView = findViewById<TextView>(R.id.tvGroupNameDashboard)
        val groupImageView = findViewById<ImageView>(R.id.ivGroupImageDashboard)
        val feedRecycler = findViewById<RecyclerView>(R.id.recyclerViewGroupFeed)
        val challengeCardActive = findViewById<View>(R.id.challenge_card_active)
        val btnCreateChallenge = findViewById<Button>(R.id.btn_create_challenge)
        val challengeLaufzeit = findViewById<TextView>(R.id.tv_challenge_laufzeit)
        val deadMembersLayout = findViewById<LinearLayout>(R.id.ll_dead_members)
        val aufgabeDescText = findViewById<TextView>(R.id.tv_aufgabe_desc)
        val countdownText = findViewById<TextView>(R.id.tv_remaining_time)

        // Adapter-Setup mit Voting-Callback
        groupFeedAdapter = GroupFeedAdapter(emptyList()) { beitragId, vote ->
            val userIdLocal = prefs.getString("USER_ID", null)
            if (userIdLocal != null) {
                groupViewModel.vote(beitragId, vote, userIdLocal, groupId)
            } else {
                Toast.makeText(this, "UserId oder GroupId fehlt!", Toast.LENGTH_SHORT).show()
            }
        }
        feedRecycler.layoutManager = LinearLayoutManager(this)
        feedRecycler.adapter = groupFeedAdapter

        // Daten für Gruppe & Aufgabe laden
        groupViewModel.loadGroupDataWithTask(groupId, userId)

        // Gruppendetails beobachten
        lifecycleScope.launch {
            groupViewModel.groupDetails.collect { gruppe ->
                gruppe?.let {
                    groupNameTextView.text = it.gruppenname
                    if (!it.gruppenbild.isNullOrEmpty()) {
                        Glide.with(this@GroupDashboardActivity).load(it.gruppenbild).into(groupImageView)
                    } else {
                        groupImageView.setImageResource(R.drawable.group_profile_picture)
                    }
                }
            }
        }

        // Aktive Challenge beobachten
        lifecycleScope.launch {
            groupViewModel.activeChallenge.collect { challenge ->
                if (challenge != null && challenge.active) {
                    challengeCardActive.visibility = View.VISIBLE
                    btnCreateChallenge.visibility = View.GONE

                    deadMembersLayout.visibility =
                        if (challenge.typ == "survival") View.VISIBLE else View.GONE

                    val daysSinceStart = TimeUnit.MILLISECONDS
                        .toDays(System.currentTimeMillis() - challenge.startdatum)
                        .coerceAtLeast(0)

                    challengeLaufzeit.text = "Challenge läuft seit $daysSinceStart Tagen"
                } else {
                    challengeCardActive.visibility = View.GONE
                    // btnCreateChallenge.visibility = View.VISIBLE
                }
            }
        }

        // Gruppenfeed beobachten
        lifecycleScope.launch {
            groupViewModel.feed.collect { beitragsListe ->
                groupFeedAdapter.submitList(beitragsListe)
            }
        }

        // Fehlermeldungen anzeigen
        groupViewModel.errorMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                groupViewModel.clearError()
            }
        }

        // Offene Aufgabe anzeigen
        lifecycleScope.launch {
            groupViewModel.openTask.collect { aufgabe ->
                aufgabeDescText?.text = aufgabe?.beschreibung ?: "Keine offene Aufgabe"
            }
        }

        // Countdown anzeigen
        lifecycleScope.launch {
            groupViewModel.remainingTime.collect { remaining ->
                countdownText?.text = if (remaining != null && remaining > 0)
                    formatSeconds(remaining)
                else
                    "Abgelaufen"
            }
        }

        // Klick auf Aufgabenkarte startet RecordActivity
        challengeCardActive.setOnClickListener {
            groupViewModel.openTask.value?.let { task ->
                startActivity(createRecordIntent(task, groupId, groupNameTextView.text.toString()))
            }
        }

        // Navigation aktivieren
        setupNavigation(groupId, groupNameTextView, groupViewModel)
    }

    // Sekundenzähler formatiert in HH:mm:ss
    private fun formatSeconds(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    // Navigation & Add-Dialog-Trigger
    private fun setupNavigation(
        groupId: String,
        groupNameTextView: TextView,
        groupViewModel: GroupViewModel
    ) {
        findViewById<Button>(R.id.btn_create_challenge).setOnClickListener {
            val intent = Intent(this, CreateChallengeModeActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.ll_groupstatus).setOnClickListener {
            val intent = Intent(this, GroupstatusActivity::class.java)
            intent.putExtra("GROUP_ID", groupId)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.nav_group).setOnClickListener {
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_add).setOnClickListener {
            lifecycleScope.launch {
                val task = groupViewModel.openTask.value
                showUniversalAddDialog(groupId, groupNameTextView.text.toString(), task)
            }
        }
    }

    // Alternativer Dialog (wird aktuell nicht genutzt)
    private fun showAddActionDialog(groupId: String, groupName: String, aufgabe: Aufgabe) {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Was möchtest du tun?")
            .setMessage("Wähle eine Aktion:")
            .setPositiveButton("Aufgabe erledigen") { _, _ ->
                val intent = createRecordIntent(aufgabe, groupId, groupName)
                intent.putExtra("TASK_ID", aufgabe.aufgabeId)
                intent.putExtra("TASK_DESC", aufgabe.beschreibung)
                intent.putExtra("GROUP_ID", groupId)
                intent.putExtra("ERFUELLUNG_ID", aufgabe.erfuellungId)
                startActivity(intent)
            }
            .setNegativeButton("Challenge erstellen") { _, _ ->
                val intent = Intent(this, CreateChallengeModeActivity::class.java)
                intent.putExtra("GROUP_ID", groupId)
                startActivity(intent)
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        alertDialog.setOnShowListener {
            val titleId = resources.getIdentifier("alertTitle", "id", "android")
            alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
            alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.WHITE)
        }
        alertDialog.show()
    }
}
