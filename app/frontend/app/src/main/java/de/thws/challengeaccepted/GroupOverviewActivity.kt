package de.thws.challengeaccepted

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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

    // ViewModel initialisieren mit Datenbank & API-Service
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

    // Hilfsfunktion zur Umrechnung von dp in px
    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_overview)

        // Systemleisten einbeziehen (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        // Statusleiste oben behandeln (z. B. bei Notch oder Uhrzeit)
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

        // Navigationsleiste unten behandeln
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Padding: oben 8dp fix, unten dynamisch
            view.setPadding(
                view.paddingLeft,
                8.dpToPx(),
                view.paddingRight,
                8.dpToPx(),
            )
            insets
        }

        // Optional: Farbe für Navigationsleiste setzen
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        // Gruppenübersicht laden
        groupViewModel.loadGroupOverview()

        val recycler = findViewById<RecyclerView>(R.id.recyclerViewGroups)
        val tvGroupCount = findViewById<TextView>(R.id.tv_group_count)

        // Adapter für RecyclerView (öffnet Gruppendetail bei Klick)
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

        // ViewModel-Flow beobachten → Gruppenliste aktualisieren
        lifecycleScope.launch {
            groupViewModel.gruppen.collect { gruppenListe ->
                adapter.submitList(gruppenListe)
                tvGroupCount.text = "Aktiv in ${gruppenListe.size} Gruppen:"
            }
        }

        // --- Bottom Navigation (wie gehabt) ---
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
