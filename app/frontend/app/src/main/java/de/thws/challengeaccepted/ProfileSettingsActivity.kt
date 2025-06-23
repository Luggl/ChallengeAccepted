package de.thws.challengeaccepted

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.graphics.Color



class ProfileSettingsActivity :  AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)


        // Navigation Back
        val navBack = findViewById<ImageView>(R.id.btn_back)
        navBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

//        // Name Bearbeiten
//        val changeName = findViewById<ImageView>(R.id.change)
//        val editName = findViewById<ImageView>(R.id.et_name)
//
//        editName.setOnClickListener {
//            editName.isEnabled = true
//            editName.requestFocus()
//           // editName.setSelection(editName.text.length) // Cursor ans Ende setzen
//        }

        // Navigation Change Passwort
        val navChangePass = findViewById<Button>(R.id.btn_change_password)
        navChangePass.setOnClickListener {
            val intent = Intent(this, ProfileChangePassActivity::class.java)
            startActivity(intent)
        }

        // Navigation Change Passwort
        val navDataPriv = findViewById<Button>(R.id.btn_datenschutz)
        navDataPriv.setOnClickListener {
            val intent = Intent(this, DataPrivacyActivity::class.java)
            startActivity(intent)
        }

        // Logout
        val logoutBtn = findViewById<Button>(R.id.btn_logout)
        logoutBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Wirklich abmelden?")
            dialogBuilder.setMessage("Wirklich abmelden?")

            dialogBuilder.setPositiveButton("Ausloggen") { _, _ ->
                Toast.makeText(this, "Sie haben sich abgemeldet!", Toast.LENGTH_SHORT).show()
                // Hier deine Logik zum Ausloggen einfügen
            }

            dialogBuilder.setNeutralButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = dialogBuilder.create()

            // Hintergrund schwarz
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            // Dialog anzeigen, dann Textfarben ändern
            alertDialog.setOnShowListener {
                // Textfarbe für Titel und Nachricht
                    val titleId = resources.getIdentifier("alertTitle", "id", "android")
                    val titleView = alertDialog.findViewById<TextView>(titleId)
                    titleView?.setTextColor(Color.WHITE)

                    val messageView = alertDialog.findViewById<TextView>(android.R.id.message)
                    messageView?.setTextColor(Color.WHITE)
                // Button-Farbe
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(Color.LTGRAY)
            }

            alertDialog.show()
        }

        // Deactivate/Delete Profil
        val deleteBtn = findViewById<Button>(R.id.btn_deactivate_delete)
        deleteBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Profil wirklich löschen?")
            dialogBuilder.setMessage("Profil wirklich löschen?")

            dialogBuilder.setPositiveButton("Profil löschen") { _, _ ->
                Toast.makeText(this, "Profil wurde gelöscht", Toast.LENGTH_SHORT).show()
                // Hier deine Logik zur Löschung einfügen
            }

            // Abbrechen
            val alertDialog = dialogBuilder.create()

            // Hintergrund schwarz
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            // Text- & Buttonfarben setzen, nachdem Dialog angezeigt wurde
            alertDialog.setOnShowListener {
                    val titleId = resources.getIdentifier("alertTitle", "id", "android")
                    val titleView = alertDialog.findViewById<TextView>(titleId)
                    titleView?.setTextColor(Color.WHITE)

                    val messageView = alertDialog.findViewById<TextView>(android.R.id.message)
                    messageView?.setTextColor(Color.WHITE)

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.button_red))
            }
            alertDialog.show()
        }



        // Bottom Navigation
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
