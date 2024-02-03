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
        CollectionItem("Item 1", "10:00 AM", 500),
        CollectionItem("Item 2", "11:30 AM", 750),
        CollectionItem("Item 3", "01:45 PM", 1000),
        CollectionItem("Item 4", "11:30 AM", 750),
        CollectionItem("Item 5", "01:45 PM", 1000),
        CollectionItem("Item 6", "11:30 AM", 750),
        CollectionItem("Item 7", "01:45 PM", 1000),
        CollectionItem("Item 8", "11:30 AM", 750),
        CollectionItem("Item 9", "01:45 PM", 1000),
        CollectionItem("Item 10", "01:45 PM", 1000),
    )
    val upcomingCollectionItems = listOf(
        CollectionItem("Upcoming Item 1", "10:00 AM", 500),
        CollectionItem("Upcoming Item 2", "11:30 AM", 750),
        CollectionItem("Upcoming Item 3", "01:45 PM", 1000),
        CollectionItem("Upcoming Item 4", "11:30 AM", 750),
        CollectionItem("Upcoming Item 5", "01:45 PM", 1000),
        CollectionItem("Upcoming Item 6", "11:30 AM", 750),
        CollectionItem("Upcoming Item 7", "01:45 PM", 1000),
        CollectionItem("Upcoming Item 8", "11:30 AM", 750),
        CollectionItem("Upcoming Item 9", "01:45 PM", 1000),
        CollectionItem("Upcoming Item 10", "01:45 PM", 1000),
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
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    item.setIcon(R.drawable.icon_home)
                    true
                }
                R.id.nav_past -> {
                    item.setIcon(R.drawable.icon_past)
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