package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.thws.challengeaccepted.ui.adapter.GroupAdapter
import de.thws.challengeaccepted.ui.viewmodels.GroupViewModel
import android.widget.ImageView
import android.widget.TextView


class GroupOverviewActivity : AppCompatActivity() {

    private val groupViewModel: GroupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_overview)

        val recycler = findViewById<RecyclerView>(R.id.recyclerViewGroups)
        recycler.layoutManager = LinearLayoutManager(this)

        val tvGroupCount = findViewById<TextView>(R.id.tv_group_count) // <- Deine ID!
        // Jetzt wird OHNE userId geladen, der Server erkennt den User über das JWT!
        groupViewModel.loadGroupOverview { groups ->
            runOnUiThread {
                recycler.adapter = GroupAdapter(groups) { group ->
                    val intent = Intent(this, GroupDashboardActivity::class.java)
                    intent.putExtra("GROUP_ID", group.gruppe_id)
                    intent.putExtra("GROUP_NAME", group.gruppenname)
                    intent.putExtra("GROUP_BESCHREIBUNG", group.beschreibung)
                    intent.putExtra("GROUP_BILD", group.gruppenbild)
                    startActivity(intent)
                }
                tvGroupCount.text = "Aktiv in ${groups.size} Gruppen:"
            }
        }

        // Bottom Navigation
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
