package de.thws.challengeaccepted

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import de.thws.challengeaccepted.ui.viewmodels.BeitragViewModelFactory
import de.thws.challengeaccepted.ui.viewmodels.BeitragViewModel
import de.thws.challengeaccepted.data.repository.BeitragRepository
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import androidx.activity.viewModels


class PostEditActivity : AppCompatActivity() {

    private lateinit var beitragViewModel: BeitragViewModel
    private var erfuellungId: String? = null

    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()


//    Hilfsfunktion um Uri in File zu konvertieren
    private fun uriToFile(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", "mp4", cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return tempFile

    }

    private fun uploadVideo(
        file: File,
        beschreibung: String,
        erfuellungId: String
    ) {
        Log.d("PostEditActivity", "Starte Video-Upload innerhalb der Methode uploadVideo")
        Log.d("PostEditActivity", "ErfuellungId: $erfuellungId, Beschreibung: $beschreibung")

        beitragViewModel.uploadBeitrag(
            erfuellungId = erfuellungId,
            beschreibung = beschreibung,
            videoFile = file
        ){ success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Beitrag hochgeladen", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, GroupDashboardActivity::class.java))
                    intent.putExtra("GROUP_ID", intent.getStringExtra("GROUP_ID"))
                    finish()
                }else{
                    Toast.makeText(this, "Fehler beim Upload", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_edit)

        // Randloses Layout aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val factory = BeitragViewModelFactory(applicationContext)
        beitragViewModel = ViewModelProvider(this, factory).get(BeitragViewModel::class.java)

        val bottomNav = findViewById<View>(R.id.bottom_navigation)

        // NAVIGATIONSBALKEN UNTEN BEHANDELN
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Padding NUR unten – oben fest (z. B. 8dp), unten dynamisch
            view.setPadding(
                view.paddingLeft,
                8.dpToPx(),
                view.paddingRight,
                8.dpToPx(),
            )
            insets
        }

        erfuellungId = intent.getStringExtra("Erfuellungs_ID")
        Log.d("PostEditActivity", "Erfuellungs_ID in onCreate: $erfuellungId")

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

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

        val videoUriString = intent.getStringExtra("video_uri")
        if (videoUriString != null) {
            val videoView = findViewById<VideoView>(R.id.video_preview)
            val uri = Uri.parse(videoUriString)


            videoView.setVideoURI(uri)
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true // optional
                videoView.start()
            }
        } else {
            Toast.makeText(this, "Kein Video gefunden", Toast.LENGTH_SHORT).show()
        }



        // Aktivität fertigstellen
        val done = findViewById<ImageView>(R.id.btn_confirm_selection)
        done.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Du bist fertig mit deiner Aktivität?")

            dialogBuilder.setPositiveButton("Abschließen") { _, _ ->
                Log.d("PostEditActivity", "Abschließen Button wurde gedrückt")
                Toast.makeText(this, "Button wurde gedrückt...", Toast.LENGTH_SHORT).show()
                Log.d("PostEditActivity", "Toast wurde angezeigt")
                val prefs = getSharedPreferences("app", MODE_PRIVATE)
                Log.d("PostEditActivity", "prefs wurden erzeugt")
                val userId = prefs.getString("USER_ID", null)
                Log.d("PostEditActivity", "userid wurde geholt")
                val beschreibung = findViewById<EditText>(R.id.et_group_description).text.toString()
                Log.d("PostEditActivity", "Beschreibung wurde geholt")
                val videoUriString = intent.getStringExtra("video_uri") ?: return@setPositiveButton
                Log.d("PostEditActivity", "video uri")
                val uri = Uri.parse(videoUriString)
                Log.d("PostEditActivity", "uri")
                val file = uriToFile(uri)
                Log.d("PostEditActivity", "file")


                Log.d("PostEditActivity", "Starte Video-Upload")
                Log.d("PostEditActivity", "Beschreibung: $beschreibung")
                Log.d("PostEditActivity", "userId: $userId")
                Log.d("PostEditActivity", "Erfüllung ID: $erfuellungId")
                Log.d("PostEditActivity", "Video-Datei vorhanden: ${file?.exists()} Pfad: ${file?.path}")


                if (userId != null && file != null && erfuellungId != null) {
                    Log.d("PostEditActivity", "Video-Upload gestartet")
                    Toast.makeText(this, "Upload wird gestartet...", Toast.LENGTH_SHORT).show()
                    uploadVideo(file, beschreibung, erfuellungId!!)
                } else {
                    Log.d("PostEditActivity", "Fehlende Daten für Upload: user ${userId}, file ${file}, beschreibung ${beschreibung}, erfuellungId ${erfuellungId}")
                    Toast.makeText(this, "Fehlende Daten für Upload", Toast.LENGTH_SHORT).show()
                }

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