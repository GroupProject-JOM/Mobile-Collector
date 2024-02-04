package org.jom.collector

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


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
                    item.setIcon(R.drawable.icon_user)
                    true
                }

                else -> false
            }
        }
    }
}