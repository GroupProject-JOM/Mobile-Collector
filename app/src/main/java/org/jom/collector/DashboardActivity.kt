package org.jom.collector

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import org.jom.collector.profile.ViewProfileActivity
import java.net.CookieHandler
import java.net.CookiePolicy
import java.net.HttpCookie
import java.net.URI
import okhttp3.Request


class DashboardActivity : AppCompatActivity() {

    val todayCollectionItems = listOf(
        CollectionItem("Maharagama", "10:00 AM", 500, "Cash"),
        CollectionItem("Piliyandala", "11:30 AM", 750, "Cash"),
        CollectionItem("Pannipitiya", "01:45 PM", 1000, "Bank"),
        CollectionItem("Kirulapone", "11:30 AM", 750, "Cash"),
        CollectionItem("Kohuwala", "01:45 PM", 1000, "Bank"),
        CollectionItem("Nugegoda", "11:30 AM", 750, "Bank"),
        CollectionItem("Udahamulla", "01:45 PM", 1000, "Bank"),
        CollectionItem("Wijerama", "11:30 AM", 750, "Cash"),
        CollectionItem("Navinna", "01:45 PM", 1000, "Bank"),
        CollectionItem("Delkanda", "01:45 PM", 1000, "Bank"),
    )
    val upcomingCollectionItems = listOf(
        CollectionItem("Maharagama", "2024-02-05 10:00 AM", 500, "Cash"),
        CollectionItem("Piliyandala", "2024-02-05 11:30 AM", 750, "Cash"),
        CollectionItem("Pannipitiya", "2024-02-05 01:45 PM", 1000, "Bank"),
        CollectionItem("Kirulapone", "2024-02-05 11:30 AM", 750, "Cash"),
        CollectionItem("Kohuwala", "2024-02-06 01:45 PM", 1000, "Bank"),
        CollectionItem("Nugegoda", "2024-02-06 11:30 AM", 750, "Bank"),
        CollectionItem("Udahamulla", "2024-02-06 01:45 PM", 1000, "Bank"),
        CollectionItem("Wijerama", "2024-02-06 11:30 AM", 750, "Cash"),
        CollectionItem("Navinna", "2024-02-06 01:45 PM", 1000, "Bank"),
        CollectionItem("Delkanda", "2024-02-06 01:45 PM", 1000, "Bank"),
    )

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recycleViewToday: RecyclerView
    private lateinit var recycleViewUpcoming: RecyclerView
    private lateinit var todayCollectionsAdapter: CollectionsAdapter
    private lateinit var upcomingCollectionsAdapter: CollectionsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val cookieManager = CookieManager.getInstance()

        val cookies = getAllCookies(cookieManager)
        for (cookie in cookies) {
            Log.d("Cookie", "${cookie.first}=${cookie.second}")
        }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)

        // recycle views handle
        recycleViewToday = findViewById(R.id.recycleViewToday)
        recycleViewUpcoming = findViewById(R.id.recycleViewUpcoming)

        recycleViewToday.layoutManager = LinearLayoutManager(this)
        recycleViewUpcoming.layoutManager = LinearLayoutManager(this)

        todayCollectionsAdapter = CollectionsAdapter(todayCollectionItems)
        recycleViewToday.adapter = todayCollectionsAdapter

        upcomingCollectionsAdapter = CollectionsAdapter(upcomingCollectionItems)
        recycleViewUpcoming.adapter = upcomingCollectionsAdapter

        // mid nav handle
        var today: Button = findViewById(R.id.today)
        var upcoming: Button = findViewById(R.id.upcoming)
        today.setOnClickListener() {
            recycleViewToday.visibility = View.VISIBLE
            recycleViewUpcoming.visibility = View.GONE

            today.setTypeface(null, Typeface.BOLD)
            upcoming.setTypeface(null, Typeface.NORMAL)
            today.setTextColor(ContextCompat.getColor(this, R.color.lightSidebarColor))
            upcoming.setTextColor(ContextCompat.getColor(this, R.color.lightPrimaryColor))
        }
        upcoming.setOnClickListener() {
            recycleViewToday.visibility = View.GONE
            recycleViewUpcoming.visibility = View.VISIBLE

            upcoming.setTypeface(null, Typeface.BOLD)
            today.setTypeface(null, Typeface.NORMAL)
            upcoming.setTextColor(ContextCompat.getColor(this, R.color.lightSidebarColor))
            today.setTextColor(ContextCompat.getColor(this, R.color.lightPrimaryColor))
        }

        // bottom nav handler
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
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
                    val intent = Intent(this, ViewProfileActivity::class.java)
                    startActivity(intent)

                    item.setIcon(R.drawable.icon_user)
                    true
                }

                else -> false
            }
        }
    }

    fun getAllCookies(cookieManager: CookieManager): List<Pair<String, String>> {
        // Get all cookies
        val allCookies = cookieManager.getCookie("10.0.2.2")

        // Initialize a list to store the parsed cookies
        val parsedCookies = mutableListOf<Pair<String, String>>()

        // Check if allCookies is not null or empty
        if (!allCookies.isNullOrBlank()) {
            // Split the cookies string into individual cookies
            val cookieArray = allCookies.split("; ")

            // Iterate through each cookie and parse it
            for (cookie in cookieArray) {
                // Split the cookie string into name and value
                val parts = cookie.split("=")
                if (parts.size == 2) {
                    // Add the parsed cookie to the list
                    parsedCookies.add(Pair(parts[0], parts[1]))
                }
            }
        }

        return parsedCookies
    }
}