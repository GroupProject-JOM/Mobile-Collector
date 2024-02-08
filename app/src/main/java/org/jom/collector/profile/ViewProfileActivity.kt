package org.jom.collector.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jom.collector.DashboardActivity
import org.jom.collector.R

class ViewProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var backButton: ImageView
    private lateinit var edit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        //back
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { this.onBackPressed() }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)

        // edit profile
        edit = findViewById(R.id.edit)
        edit.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // bottom nav handler
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.nav_user
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)

                    item.setIcon(R.drawable.icon_home)
                    true
                }

                R.id.nav_completed -> {
                    item.setIcon(R.drawable.icon_completed)
                    true
                }

                R.id.nav_assigned -> {
                    item.setIcon(R.drawable.icon_assigned)
                    true
                }

                R.id.nav_user -> {
                    item.setIcon(R.drawable.icon_user)
                    true
                }

                else -> false
            };
        }
    }
}