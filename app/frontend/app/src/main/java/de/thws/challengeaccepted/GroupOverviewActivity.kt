package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.thws.challengeaccepted.data.database.AppDatabase
import de.thws.challengeaccepted.data.repository.GroupRepository
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.GroupService
import de.thws.challengeaccepted.ui.adapter.GroupAdapter
import de.thws.challengeaccepted.ui.viewmodels.GroupViewModel
import de.thws.challengeaccepted.ui.viewmodels.GroupViewModelFactory
import kotlinx.coroutines.launch

class GroupOverviewActivity : AppCompatActivity() {

    private val groupViewModel: GroupViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val groupService = ApiClient.getRetrofit(applicationContext).create(GroupService::class.java)
        val repository = GroupRepository(
            groupService,
            applicationContext,
            database.gruppeDao(),
            database.challengeDao(),
            database.beitragDao(),
            database.membershipDao(),
            database.userDao()
        )
        GroupViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_overview)
        groupViewModel.loadGroupOverview()
        val recycler = findViewById<RecyclerView>(R.id.recyclerViewGroups)
        val tvGroupCount = findViewById<TextView>(R.id.tv_group_count)

        // Der Adapter erwartet jetzt eine Liste von "Gruppe"-Entities
        val adapter = GroupAdapter { gruppe ->
            val intent = Intent(this, GroupDashboardActivity::class.java)
            intent.putExtra("GROUP_ID", gruppe.gruppeId)
            intent.putExtra("GROUP_NAME", gruppe.gruppenname)
            intent.putExtra("GROUP_BESCHREIBUNG", gruppe.beschreibung)
            intent.putExtra("GROUP_BILD", gruppe.gruppenbild)
            startActivity(intent)
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Beobachte den Flow aus dem ViewModel
        lifecycleScope.launch {
            groupViewModel.gruppen.collect { gruppenListe ->
                // Der ListAdapter aktualisiert die UI effizient
                adapter.submitList(gruppenListe)
                tvGroupCount.text = "Aktiv in ${gruppenListe.size} Gruppen:"
            }
        }

        // --- Bottom Navigation (bleibt wie gehabt) ---
        val navHome = findViewById<ImageView>(R.id.nav_home)
        navHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        val navAdd = findViewById<ImageView>(R.id.nav_add)
        navAdd.setOnClickListener {
            val intent = Intent(this, CreateNewGroupActivity::class.java)
            startActivity(intent)
        }

        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}