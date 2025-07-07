package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.thws.challengeaccepted.data.database.AppDatabase
import de.thws.challengeaccepted.data.repository.GroupDashboardRepository
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.GroupService
import de.thws.challengeaccepted.ui.adapter.MemberGridAdapter
import de.thws.challengeaccepted.ui.viewmodels.GroupstatusViewModel
import de.thws.challengeaccepted.ui.viewmodels.GroupstatusViewModelFactory
import kotlinx.coroutines.launch

class GroupstatusActivity : AppCompatActivity() {

    // KORREKT: ViewModel wird mit der Factory korrekt initialisiert
    private val viewModel: GroupstatusViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val service = ApiClient.getRetrofit(applicationContext).create(GroupService::class.java)
        val repo = GroupDashboardRepository(service, db.gruppeDao(), db.challengeDao(), db.membershipDao())
        GroupstatusViewModelFactory(repo, db.gruppeDao(), db.membershipDao(), db.userDao())
    }

    private lateinit var memberAdapter: MemberGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupstatus)

        val groupId = intent.getStringExtra("GROUP_ID")
        if (groupId == null) {
            Toast.makeText(this, "Fehler: Gruppen-ID fehlt", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Views initialisieren
        val groupBanner = findViewById<ImageView>(R.id.iv_group_banner)
        val groupName = findViewById<TextView>(R.id.tv_group_name_detail)
        val groupDescription = findViewById<TextView>(R.id.tv_group_description)
        val memberRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_group_members)

        // Adapter und RecyclerView für die Mitgliederliste einrichten
        memberAdapter = MemberGridAdapter()
        memberRecyclerView.layoutManager = GridLayoutManager(this, 2) // 2 Spalten für das Gitter
        memberRecyclerView.adapter = memberAdapter

        // Daten laden
        viewModel.loadGroupDetails(groupId)

        // UI beobachten und mit Daten füllen
        observeViewModel(groupName, groupBanner, groupDescription)

        // Navigation und Buttons einrichten
        setupNavigationAndButtons(groupId)
    }

    private fun observeViewModel(groupName: TextView, groupBanner: ImageView, groupDescription: TextView) {
        // Beobachtet die Gruppendetails (Name, Bild, Beschreibung)
        lifecycleScope.launch {
            viewModel.groupDetails.collect { gruppe ->
                gruppe?.let {
                    groupName.text = it.gruppenname
                    groupDescription.text = it.beschreibung ?: "Keine Beschreibung vorhanden."
                    if (!it.gruppenbild.isNullOrEmpty()) {
                        Glide.with(this@GroupstatusActivity).load(it.gruppenbild).into(groupBanner)
                    } else {
                        groupBanner.setImageResource(R.drawable.group_profile_picture) // Fallback-Bild
                    }
                }
            }
        }

        // Beobachtet die Mitgliederliste
        lifecycleScope.launch {
            viewModel.members.collect { memberList ->
                memberAdapter.submitList(memberList)
            }
        }
    }

    private fun setupNavigationAndButtons(groupId: String) {
        // Zurück-Button
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Freunde einladen Button
        findViewById<Button>(R.id.btn_invite_friends).setOnClickListener {
            // Hier Logik für Einladungslink implementieren
            Toast.makeText(this, "Freunde einladen...", Toast.LENGTH_SHORT).show()
        }

        // Challenge Overview Button
        findViewById<Button>(R.id.btn_challenge_overview).setOnClickListener {
            val intent = Intent(this, GroupDashboardActivity::class.java)
            intent.putExtra("GROUP_ID", groupId)
            startActivity(intent)
        }

        // Gruppe verlassen Button
        findViewById<Button>(R.id.btn_leave_group).setOnClickListener {
            showLeaveGroupDialog()
        }

        // Bottom Navigation
        findViewById<ImageView>(R.id.nav_group).setOnClickListener {
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        findViewById<ImageView>(R.id.nav_add).setOnClickListener {
            // Hier Logik für "Add"-Button einfügen
        }
        findViewById<ImageView>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun showLeaveGroupDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gruppe verlassen")
            .setMessage("Möchtest du diese Gruppe wirklich verlassen?")
            .setPositiveButton("Verlassen") { _, _ ->
                // Hier Logik zum Verlassen der Gruppe implementieren (API-Call etc.)
                Toast.makeText(this, "Gruppe wird verlassen...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Abbrechen", null)
            .show()
    }
}
