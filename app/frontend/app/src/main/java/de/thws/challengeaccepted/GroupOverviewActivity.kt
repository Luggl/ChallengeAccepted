package de.thws.challengeaccepted

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class GroupOverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_overview)

        // Group Dashbord
        val navToGroup = findViewById<LinearLayout>(R.id.group_list)
        navToGroup.setOnClickListener {
            val intent = Intent(this, GroupDashboardActivity::class.java)
            startActivity(intent)
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