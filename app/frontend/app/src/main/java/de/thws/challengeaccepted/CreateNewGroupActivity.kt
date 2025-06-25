package de.thws.challengeaccepted

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class CreateNewGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //Randloses Layout aktivieren (Edge-to-Edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_group)

        // Gruppe erstellen Button
        val groupNameEditText = findViewById<EditText>(R.id.tv_group_name)
        val createButton = findViewById<Button>(R.id.button_finish)

        createButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()

            if (groupName.isEmpty()) {
                Toast.makeText(this, "Bitte gib einen Gruppennamen ein", Toast.LENGTH_SHORT).show()
            } else {
                // Beispiel Link
                val inviteLink = "https://meineapp.de/einladung/abc123"
                val dialogView = layoutInflater.inflate(R.layout.popup_group_created, null)

                val tvInviteLink = dialogView.findViewById<TextView>(R.id.tvInviteLink)
                tvInviteLink.text = inviteLink

                val dialog = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create()

                val btnCopy = dialogView.findViewById<Button>(R.id.btnCopyLink)
                btnCopy.setOnClickListener {
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Einladungslink", inviteLink)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, "Link wurde kopiert", Toast.LENGTH_SHORT).show()
                }

                val btnDone = dialogView.findViewById<ImageButton>(R.id.btn_confirm_selection)
                btnDone.setOnClickListener {
                    val intent = Intent(this, GroupOverviewActivity::class.java)
                    startActivity(intent)
                }

                dialog.show()
            }
        }



        // Button Navigation
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        navGroup.setOnClickListener {
            val intent = Intent(this, GroupOverviewActivity::class.java)
            startActivity(intent)
        }

        val navHome = findViewById<ImageView>(R.id.nav_home)
        navHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}