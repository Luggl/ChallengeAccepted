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
import de.thws.challengeaccepted.data.entities.Aufgabe

class GroupDashboardActivity : AppCompatActivity() {

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

    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    // --- HILFSFUNKTION FÜR INTENT ---
    private fun createRecordIntent(aufgabe: Aufgabe, groupId: String, groupName: String): Intent {
        return Intent(this, RecordActivity::class.java).apply {
            putExtra("TASK_ID", aufgabe.aufgabeId)
            putExtra("TASK_DESC", aufgabe.beschreibung)
            putExtra("GROUP_ID", groupId)
            putExtra("GROUP_NAME", groupName)
            putExtra("Erfuellungs_ID", aufgabe.erfuellungsId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_dashboard)

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

        val groupId = intent.getStringExtra("GROUP_ID")
        if (groupId == null) {
            Toast.makeText(this, "Fehler: Gruppen-ID fehlt", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val userId = prefs.getString("USER_ID", null)
        if (userId == null) {
            Toast.makeText(this, "Fehler: User-ID fehlt", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Views initialisieren
        val groupNameTextView = findViewById<TextView>(R.id.tvGroupNameDashboard)
        val groupImageView = findViewById<ImageView>(R.id.ivGroupImageDashboard)
        val feedRecycler = findViewById<RecyclerView>(R.id.recyclerViewGroupFeed)
        val challengeCardActive = findViewById<View>(R.id.challenge_card_active)
        val btnCreateChallenge = findViewById<Button>(R.id.btn_create_challenge)
        val challengeLaufzeit = findViewById<TextView>(R.id.tv_challenge_laufzeit)
        val deadMembersLayout = findViewById<LinearLayout>(R.id.ll_dead_members)
        val aufgabeDescText = findViewById<TextView>(R.id.tv_aufgabe_desc)
        val countdownText = findViewById<TextView>(R.id.tv_remaining_time)

        // Voting-Adapter
        groupFeedAdapter = GroupFeedAdapter(emptyList()) { beitragId, vote ->
            val prefs = getSharedPreferences("app", MODE_PRIVATE)
            val userId = prefs.getString("USER_ID", null)
            if (userId != null && groupId != null) {
                groupViewModel.vote(beitragId, vote, userId, groupId)
            } else {
                Toast.makeText(this, "UserId oder GroupId fehlt!", Toast.LENGTH_SHORT).show()
            }
        }
        feedRecycler.layoutManager = LinearLayoutManager(this)
        feedRecycler.adapter = groupFeedAdapter

        // --- Lade Gruppe + offene Aufgabe + Timer! ---
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

                    if (challenge.typ == "survival") {
                        deadMembersLayout.visibility = View.VISIBLE
                    } else {
                        deadMembersLayout.visibility = View.GONE
                    }

                    val daysSinceStart = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - challenge.startdatum).coerceAtLeast(0)
                    challengeLaufzeit.text = "Challenge läuft seit $daysSinceStart Tagen"
                } else {
                    challengeCardActive.visibility = View.GONE
                    btnCreateChallenge.visibility = View.VISIBLE
                }
            }
        }

        // Feed beobachten
        lifecycleScope.launch {
            groupViewModel.feed.collect { beitragsListe ->
                groupFeedAdapter.submitList(beitragsListe)
            }
        }

        // Fehlermeldungen
        groupViewModel.errorMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(this@GroupDashboardActivity, msg, Toast.LENGTH_SHORT).show()
                groupViewModel.clearError()
            }
        }

        // Offene Aufgabe beobachten & anzeigen
        lifecycleScope.launch {
            groupViewModel.openTask.collect { aufgabe ->
                if (aufgabe != null) {
                    aufgabeDescText?.text = aufgabe.beschreibung
                } else {
                    aufgabeDescText?.text = "Keine offene Aufgabe"
                }
            }
        }

        lifecycleScope.launch {
            groupViewModel.remainingTime.collect { remaining ->
                if (remaining != null && remaining > 0) {
                    countdownText?.text = formatSeconds(remaining)
                } else {
                    countdownText?.text = "Abgelaufen"
                }
            }
        }

        // --- Challenge Card OnClick ---
        challengeCardActive.setOnClickListener {
            groupViewModel.openTask.value?.let { aufgabe ->
                val intent = createRecordIntent(aufgabe, groupId, groupNameTextView.text.toString())
                startActivity(intent)
            }
        }

        // --- Navigation ---
        setupNavigation(groupId, groupNameTextView, groupViewModel)
    }

    private fun formatSeconds(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

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
            val aufgabe = groupViewModel.openTask.value
            if (aufgabe != null) {
                showAddActionDialog(
                    groupId,
                    groupNameTextView.text.toString(),
                    aufgabe
                )
            } else {
                Toast.makeText(this, "Keine offene Aufgabe gefunden!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddActionDialog(groupId: String, groupName: String, aufgabe: Aufgabe) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Was möchtest du tun?")
            .setMessage("Wähle eine Aktion:")
            .setPositiveButton("Aufgabe erledigen") { _, _ ->
                Toast.makeText(this, "Aufgaben-Erledigungs-Flow startet...", Toast.LENGTH_SHORT).show()
                val intent = createRecordIntent(aufgabe, groupId, groupName)
                startActivity(intent)
            }
            .setNegativeButton("Challenge erstellen") { _, _ ->
                Toast.makeText(this, "Challenge-Erstellung startet...", Toast.LENGTH_SHORT).show()
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
}
