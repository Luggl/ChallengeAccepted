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
import de.thws.challengeaccepted.models.GroupCreateRequest
import de.thws.challengeaccepted.models.GroupResponse
import de.thws.challengeaccepted.models.CreateGroupResponse
import de.thws.challengeaccepted.network.ApiClient
import de.thws.challengeaccepted.network.GroupService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNewGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_group)

        val groupNameEditText = findViewById<EditText>(R.id.tv_group_name)
        val groupDescriptionEditText = findViewById<EditText>(R.id.et_group_description)
        val createButton = findViewById<Button>(R.id.button_finish)

        createButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()
            val description = groupDescriptionEditText.text.toString().trim()

            if (groupName.isEmpty()) {
                Toast.makeText(this, "Bitte gib einen Gruppennamen ein", Toast.LENGTH_SHORT).show()
            } else {
                // --- ECHTER API-CALL ---
                val service = ApiClient.getRetrofit(this).create(GroupService::class.java)
                val request = GroupCreateRequest(
                    name = groupName,
                    beschreibung = if (description.isEmpty()) null else description,
                    gruppenbild = null // optional, falls noch kein Bild
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = service.createGroup(request)
                        val gruppe = response.gruppe

                        runOnUiThread {
                            val dialogView = layoutInflater.inflate(R.layout.popup_group_created, null)
                            val tvGroupCreated = dialogView.findViewById<TextView>(R.id.tvGroupCreated)
                            tvGroupCreated.text = "Gruppe wurde erfolgreich erstellt!"

                            val tvInviteLink = dialogView.findViewById<TextView>(R.id.tvInviteLink)
                            tvInviteLink.text = "ID: ${gruppe.id}\nName: ${gruppe.name}\nInvite-Link: ${gruppe.invite_link}"

                            val dialog = AlertDialog.Builder(this@CreateNewGroupActivity)
                                .setView(dialogView)
                                .setCancelable(true)
                                .create()

                            val btnCopy = dialogView.findViewById<Button>(R.id.btnCopyLink)
                            btnCopy.setOnClickListener {
                                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Gruppen-ID", gruppe.id)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(this@CreateNewGroupActivity, "ID kopiert", Toast.LENGTH_SHORT).show()
                            }

                            val btnDone = dialogView.findViewById<ImageButton>(R.id.btn_confirm_selection)
                            btnDone.setOnClickListener {
                                val intent = Intent(this@CreateNewGroupActivity, GroupOverviewActivity::class.java)
                                startActivity(intent)
                            }

                            dialog.show()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@CreateNewGroupActivity, "Fehler beim Erstellen: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        // Navigation
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)

        navGroup.setOnClickListener {
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        navHome.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
