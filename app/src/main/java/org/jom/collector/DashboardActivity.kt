package org.jom.collector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashboardActivity : AppCompatActivity() {

    val collectionItems = listOf(
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
        // Add more items as needed
    )

    private  lateinit var bottomNavigationView: BottomNavigationView
//    private  lateinit var bottomNavigationHome: BottomNavigationItemView
//    private  lateinit var bottomNavigationUser: BottomNavigationItemView

    private lateinit var collectionsRecyclerView: RecyclerView
    private lateinit var collectionsAdapter: CollectionsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        collectionsRecyclerView = findViewById(R.id.recycleViewToday)
        collectionsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Assuming you have a data class CollectionItem and a data list
//        val collectionItems = listOf<CollectionItem>() // Replace with actual data
        collectionsAdapter = CollectionsAdapter(collectionItems)
        collectionsRecyclerView.adapter = collectionsAdapter


        bottomNavigationView = findViewById(R.id.bottom_navigation)
//        bottomNavigationHome = findViewById(R.id.nav_home)
//        bottomNavigationUser = findViewById(R.id.nav_user)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightButtonHover)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightButtonHover)

//        bottomNavigationUser.setOnClickListener{
//            Toast.makeText(applicationContext,"Profile", Toast.LENGTH_SHORT).show()
////            bottomNavigationUser.isActivated = true
////            bottomNavigationUser.setTextAppearanceActive()
//        }


//        var scrollContainer1 : ScrollView = findViewById(R.id.scrollContainer1)
//        var scrollContainer2 : ScrollView = findViewById(R.id.scrollContainer2)
//
//        var button01 : Button = findViewById(R.id.button01)
//        var button02 :Button = findViewById(R.id.button02)
//
//        button01.setOnClickListener(){
//            scrollContainer1.visibility = View.VISIBLE
//            scrollContainer2.visibility = View.GONE
//        }
//
//        button02.setOnClickListener(){
//            scrollContainer1.visibility = View.GONE
//            scrollContainer2.visibility = View.VISIBLE
//        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    item.setIcon(R.drawable.icon_home)
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
    private fun setCustomLayout(item: MenuItem, text: String) {
        val customView = LayoutInflater.from(this).inflate(R.layout.custom_bottom_navigation_item, null)
        val textView: TextView = customView.findViewById(R.id.custom_text)
        textView.text = text
        item.setActionView(customView)
    }
}