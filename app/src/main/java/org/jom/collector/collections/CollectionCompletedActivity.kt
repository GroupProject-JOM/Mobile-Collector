package org.jom.collector.collections

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jom.collector.AssignCollectionsActivity
import org.jom.collector.CompletedCollectionsActivity
import org.jom.collector.DashboardActivity
import org.jom.collector.R
import org.jom.collector.profile.ViewProfileActivity

class CollectionCompletedActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var backButton: ImageView
    private lateinit var dashboard: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_completed)

        // initialize text views
        val collection_id: TextView = findViewById(R.id.collection_id)
        val name: TextView = findViewById(R.id.name)
        val phone: TextView = findViewById(R.id.phone)
        val estate: TextView = findViewById(R.id.estate)
        val date: TextView = findViewById(R.id.date)
        val time: TextView = findViewById(R.id.time)
        val amount: TextView = findViewById(R.id.count)
        val actual: TextView = findViewById(R.id.actual)
        val payment: TextView = findViewById(R.id.payment)

        // get intent instance and bundle
        var i = intent
        var extras = i.extras!!

        // get put data from bundle and assign to text views
        collection_id.text = extras.getString("id")
        name.text = extras.getString("name")
        phone.text = extras.getString("phone")
        estate.text = extras.getString("address")
        date.text = extras.getString("date")
        time.text = extras.getString("time")
        amount.text = extras.getString("amount")
        actual.text = extras.getString("actual")
        payment.text = extras.getString("payment")

        //back
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { this.onBackPressed() }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)

        // back to dashboard
        dashboard = findViewById(R.id.dashboard)
        dashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        // bottom nav handler
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.nav_assigned
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)

                    item.setIcon(R.drawable.icon_home)
                    true
                }

                R.id.nav_completed -> {
                    val intent = Intent(this, CompletedCollectionsActivity::class.java)
                    startActivity(intent)

                    item.setIcon(R.drawable.icon_completed)
                    true
                }

                R.id.nav_assigned -> {
                    val intent = Intent(this, AssignCollectionsActivity::class.java)
                    startActivity(intent)

                    item.setIcon(R.drawable.icon_assigned)
                    true
                }

                R.id.nav_user -> {
                    val intent = Intent(this, ViewProfileActivity::class.java)
                    startActivity(intent)

                    item.setIcon(R.drawable.icon_user)
                    true
                }

                else -> false
            };
        }
    }
}