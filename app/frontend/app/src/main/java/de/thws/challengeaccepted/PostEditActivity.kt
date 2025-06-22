package de.thws.challengeaccepted

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class PostEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_edit)

        // Abbruch
        val navCanc = findViewById<ImageView>(R.id.btn_cancel)
        navCanc.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Möchtest du die Aktivität wirklich abbrechen?")

            dialogBuilder.setPositiveButton("Abbrechen") { _, _ ->
                Toast.makeText(this, "Aktivität wurde abgebrochen", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, GroupDashboardActivity::class.java)
                startActivity(intent)
            }

            // Abbrechen
            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            alertDialog.setOnShowListener {
                // Titel & Nachricht in weiß
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)

                // Buttonfarben
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.button_red))
            }

            alertDialog.show()
        }


        // Aktivität fertigstellen
        val done = findViewById<ImageView>(R.id.btn_confirm_selection)
        done.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Du bist fertig mit deiner Aktivität?")

            dialogBuilder.setPositiveButton("Abschließen") { _, _ ->
                Toast.makeText(this, "Aktivität wurde abgeschlossen!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, GroupDashboardActivity::class.java)
                startActivity(intent)
            }

            // Abbrechen
            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            alertDialog.setOnShowListener {
                // Titel & Nachricht in weiß
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)

                // Buttonfarben
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.button_green))
            }

            alertDialog.show()
        }


        // Bottom Navigation
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        navGroup.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Möchtest du die Aktivität wirklich abbrechen?")

            dialogBuilder.setPositiveButton("Abbrechen") { _, _ ->
                Toast.makeText(this, "Aktivität wurde abgebrochen", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, GroupOverviewActivity::class.java)
                startActivity(intent)
            }

            // Abbrechen
            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            alertDialog.setOnShowListener {
                // Titel & Nachricht in weiß
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)

                // Buttonfarben
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.button_red))
            }

            alertDialog.show()
        }

        val navHome = findViewById<ImageView>(R.id.nav_home)
        navHome.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Möchtest du die Aktivität wirklich abbrechen?")

            dialogBuilder.setPositiveButton("Abbrechen") { _, _ ->
                Toast.makeText(this, "Aktivität wurde abgebrochen", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }

            // Abbrechen
            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            alertDialog.setOnShowListener {
                // Titel & Nachricht in weiß
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)

                // Buttonfarben
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.button_red))
            }

            alertDialog.show()
        }

        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Möchtest du die Aktivität wirklich abbrechen?")

            dialogBuilder.setPositiveButton("Abbrechen") { _, _ ->
                Toast.makeText(this, "Aktivität wurde abgebrochen", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }

            // Abbrechen
            val alertDialog = dialogBuilder.create()

            // Schwarzer Hintergrund
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

            alertDialog.setOnShowListener {
                // Titel & Nachricht in weiß
                val titleId = resources.getIdentifier("alertTitle", "id", "android")
                alertDialog.findViewById<TextView>(titleId)?.setTextColor(Color.WHITE)
                alertDialog.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.WHITE)

                // Buttonfarben
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.button_red))
            }

            alertDialog.show()
        }
    }
}