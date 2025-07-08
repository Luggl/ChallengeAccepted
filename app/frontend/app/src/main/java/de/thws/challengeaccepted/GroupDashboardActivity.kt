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

class GroupDashboardActivity : AppCompatActivity() {

    private val groupViewModel: GroupViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val service = ApiClient.getRetrofit(applicationContext).create(GroupService::class.java)
        val repository = GroupRepository(
            service,
            db.gruppeDao(),
            db.challengeDao(),
            db.beitragDao(),
            db.membershipDao(),
            db.userDao()
        )
        GroupViewModelFactory(repository)
    }

    private lateinit var groupFeedAdapter: GroupFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_dashboard)

        val groupId = intent.getStringExtra("GROUP_ID")
        if (groupId == null) {
            Toast.makeText(this, "Fehler: Gruppen-ID fehlt", Toast.LENGTH_LONG).show()
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

        // Voting-Adapter mit Callback für Accepted/Rejected
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

        // --- Daten Laden & UI Beobachten ---
        groupViewModel.loadGroupData(groupId)

        // Gruppendetails beobachten (Header!)
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

        // Feed beobachten (und Voting updaten)
        lifecycleScope.launch {
            groupViewModel.feed.collect { beitragsListe ->
                groupFeedAdapter.submitList(beitragsListe)
            }
        }

        setupNavigation(groupId)
    }

    private fun setupNavigation(groupId: String) {
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
            showAddActionDialog(groupId)
        }
    }

    private fun showAddActionDialog(groupId: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Was möchtest du tun?")
            .setMessage("Wähle eine Aktion:")
            .setPositiveButton("Aufgabe erledigen") { _, _ ->
                Toast.makeText(this, "Aufgaben-Erledigungs-Flow startet...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RecordActivity::class.java)
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
