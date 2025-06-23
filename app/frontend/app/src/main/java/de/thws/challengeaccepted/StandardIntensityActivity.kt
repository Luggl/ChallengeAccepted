package de.thws.challengeaccepted
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StandardIntensityActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //verknüpft diese Activity mit dem XML Layout
        setContentView(R.layout.activity_standard_intensity)

        //zurück Button
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        //wenn der Button geklickt wird, wird die aktuelle Activity beendet
        btnBack.setOnClickListener {
            finish()
        }
        //bestätige Button
        val btnConfirm = findViewById<ImageButton>(R.id.btn_confirm_selection)
        //beim Click wird die Start und Endintensität ausgelesen und angezeigt
        btnConfirm.setOnClickListener {
            //Startintensität aus dem Textview lesen
            val startTextView = findViewById<TextView>(R.id.tv_start_main)
            val startValue = startTextView.text.toString().toIntOrNull() ?: 0

            //Endintensität aus dem Textview lesen
            val endTextView = findViewById<TextView>(R.id.tv_end_main)
            val endValue = endTextView.text.toString().toIntOrNull() ?: 0

            //Test Ausgabe
            Toast.makeText(this, "Start: $startValue, Ende: $endValue", Toast.LENGTH_SHORT).show()
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

        val navProfile = findViewById<ImageView>(R.id.nav_profile)
        navProfile.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
