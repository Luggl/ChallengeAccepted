package de.thws.challengeaccepted
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat


class CreateChallengeModeActivity : AppCompatActivity() {
    //Modusauswahl (Standard voreingestellt)
    private var selectedMode: String="standard"

    //Views als Properties für späteren Zugriff
    //private lateinit var imageStandard: ImageView
    //private lateinit var imageSurvival: ImageView

    private lateinit var flStandard: FrameLayout
    private lateinit var flSurvival: FrameLayout
    private lateinit var tvStandard: TextView
    private lateinit var tvSurvival: TextView

    fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_challenge_mode)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootScroll = findViewById<View>(R.id.root_scroll)
        val bottomNav = findViewById<View>(R.id.bottom_navigation)

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

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                view.paddingLeft,
                8.dpToPx(),
                view.paddingRight,
                8.dpToPx(),
            )
            insets
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)


        //Views aus dem XML holen
        val navBack = findViewById<ImageView>(R.id.btn_back)
        val confirmButton=findViewById<ImageButton>(R.id.btn_confirm_selection)
        val groupId = intent.getStringExtra("groupId") ?: ""
        flStandard=findViewById(R.id.fl_standard)
        flSurvival=findViewById(R.id.fl_survival)
        tvStandard = findViewById(R.id.tv_standard)
        tvSurvival = findViewById(R.id.tv_survival)
      //  imageStandard=findViewById(R.id.iv_standard)
        //imageSurvival=findViewById(R.id.iv_survival)


        //zutück zur vorherigen Seite
        navBack.setOnClickListener {
            val intent = Intent(this, GroupDashboardActivity::class.java)
            startActivity(intent)
        }

        //Start-Markierung für Standard-Modus setzen, wenn Layout fertig ist
        flStandard.post{
            updateSelectedModeUI()
        }

        //manuelle Auswahl- wenn Nutzer etwas anderes auswählt
        flStandard.setOnClickListener{
            selectedMode="standard"
            updateSelectedModeUI()
        }
        flSurvival.setOnClickListener{
            selectedMode="survival"
            updateSelectedModeUI()
        }
        //auswahl bestätigen
        confirmButton.setOnClickListener {
            val intent = when (selectedMode) {
                "standard" -> Intent(this, StandardActivitiesActivity::class.java)
                "survival" -> Intent(this, SurvivalActivitiesActivity::class.java)
                else -> null
            }

            if (intent != null) {
                intent.putExtra("groupId", groupId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Ungültiger Modus", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialisieren
        val navGroup = findViewById<ImageView>(R.id.nav_group)
        val navHome = findViewById<ImageView>(R.id.nav_home)
        val navProfile = findViewById<ImageView>(R.id.nav_profile)

        navGroup.setOnClickListener{
            startActivity(Intent(this, GroupOverviewActivity::class.java))
        }
        navHome.setOnClickListener{
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        navProfile.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
    //Funktion zur visuellen Hervorhebung des ausgewählten Modus
    private fun updateSelectedModeUI() {
        val activeFrame= R.drawable.blue_frame
        val inactiveFrame=R.drawable.bright_grey_frame
        flStandard.setBackgroundResource(
        if (selectedMode == "standard")
            activeFrame else inactiveFrame
        )
        flSurvival.setBackgroundResource(
        if (selectedMode=="survival")
            activeFrame else inactiveFrame
        )
        tvStandard.setBackgroundResource(
            if(selectedMode=="standard")
                activeFrame else inactiveFrame
        )
        tvSurvival.setBackgroundResource(
            if (selectedMode=="survival")
            activeFrame else inactiveFrame
        )
    }
}