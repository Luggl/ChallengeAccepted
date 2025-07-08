package de.thws.challengeaccepted

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class RecordActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var recordButton: ImageButton
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var isRecording = false

    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        // Randloses Layout aktivieren
        WindowCompat.setDecorFitsSystemWindows(window, false)


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


        previewView = findViewById(R.id.cv_video)
        recordButton = findViewById(R.id.btnPlay)


        if (allPermissionsGranted()) {
            startCamera()
        } else {
//            val REQUIRED_PERMISSIONS = null
            requestPermissions.launch(REQUIRED_PERMISSIONS)
        }

        findViewById<ImageButton>(R.id.btn_switch_camera).setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }

            startCamera() // Kamera mit neuer Einstellung neu starten
        }

        recordButton.setOnClickListener {
            if (isRecording) {
                // Aufnahme beenden
                recording?.stop()
                recording = null
                isRecording = false
                recordButton.setImageResource(R.drawable.play_icon)
            } else {
                // Aufnahme starten
                val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY)
                    .format(System.currentTimeMillis()) + ".mp4"
                val outputOptions = FileOutputOptions.Builder(
                    File(getExternalFilesDir(null), name)
                ).build()

                if (allPermissionsGranted()) {
                    recording = videoCapture?.output
                        ?.prepareRecording(this, outputOptions)
                        ?.apply {
                            if (ContextCompat.checkSelfPermission(this@RecordActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                withAudioEnabled()
                            }
                        }

                        ?.start(ContextCompat.getMainExecutor(this)) { event ->
                            if (event is VideoRecordEvent.Finalize) {
                                Toast.makeText(this, "Video gespeichert", Toast.LENGTH_SHORT).show()

                                val savedUri = event.outputResults.outputUri
                                val intent = Intent(this, PostEditActivity::class.java)
                                intent.putExtra("video_uri", savedUri.toString())
                                startActivity(intent)

                            }
                        }

                    isRecording = true
                    recordButton.setImageResource(R.drawable.stop_icon)
                }
            }
        }


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
                    alertDialog.findViewById<TextView>(android.R.id.message)
                        ?.setTextColor(Color.WHITE)

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
                    Toast.makeText(this, "Aktivität wurde abgeschlossen!", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, PostEditActivity::class.java)
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
                    alertDialog.findViewById<TextView>(android.R.id.message)
                        ?.setTextColor(Color.WHITE)

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
                    alertDialog.findViewById<TextView>(android.R.id.message)
                        ?.setTextColor(Color.WHITE)

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
                    alertDialog.findViewById<TextView>(android.R.id.message)
                        ?.setTextColor(Color.WHITE)

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
                    alertDialog.findViewById<TextView>(android.R.id.message)
                        ?.setTextColor(Color.WHITE)

                    // Buttonfarben
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        ?.setTextColor(getColor(R.color.button_red))
                }

                alertDialog.show()
            }
        }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    videoCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Fehler: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            Toast.makeText(this, "Berechtigungen fehlen", Toast.LENGTH_SHORT).show()
        }
    }

     companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}

