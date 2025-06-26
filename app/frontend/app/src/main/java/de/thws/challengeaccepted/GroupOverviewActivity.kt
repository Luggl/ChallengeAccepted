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

class GroupOverviewActivity : AppCompatActivity() {

    private val groupViewModel: GroupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_overview)

        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        val userId = prefs.getString("USER_ID", null)
        val token = prefs.getString("token", null)

        val recycler = findViewById<RecyclerView>(R.id.recyclerViewGroups)
        recycler.layoutManager = LinearLayoutManager(this)

        if (userId != null) {
            groupViewModel.getGroups(userId) { groups ->
                runOnUiThread {
                    recycler.adapter = GroupAdapter(groups) { group ->
                        val intent = Intent(this, GroupDashboardActivity::class.java)
                        intent.putExtra("GROUP_ID", group.gruppe_id)
                        startActivity(intent)
                    }
                }
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