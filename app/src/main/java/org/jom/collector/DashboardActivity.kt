package org.jom.collector

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.jom.collector.profile.ViewProfileActivity
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import java.time.LocalTime

interface DashboardApi {
    @GET("JOM_war_exploded/collector")
    fun getData(): Call<ResponseBody>
}

class DashboardActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recycleViewToday: RecyclerView
    private lateinit var recycleViewUpcoming: RecyclerView
    private lateinit var todayCollectionsAdapter: CollectionsAdapter
    private lateinit var upcomingCollectionsAdapter: CollectionsAdapter
    private lateinit var jwt: String

    val todayCollectionItems = mutableListOf<CollectionItem>()
    val upcomingCollectionItems = mutableListOf<CollectionItem>()

    // get instance of methods class
    val methods = Methods()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // get cookie operations
        val cookieManager = CookieManager.getInstance()
        val cookies = methods.getAllCookies(cookieManager)

        // get jwt from cookie
        for (cookie in cookies) {
            if (cookie.first == "jwt") {
                jwt = cookie.second
            }
        }
        val cookiesMap = mapOf(
            "jwt" to jwt,
        )

        // bind jwt for request
        val client = OkHttpClient.Builder()
            .addInterceptor(AddCookiesInterceptor(cookiesMap))
            .build()

        // generate request
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8090/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val dashboardApi = retrofit.create(DashboardApi::class.java)

        // call get data function to get data from backend
        dashboardApi.getData().enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val jsonString = it.string() // Convert response body to JSON string
                        val jsonObject = JSONObject(jsonString)
                        val count = jsonObject.optString("count")
                        val rate = jsonObject.getJSONObject("rate")

                        val todayRate: TextView = findViewById(R.id.widget01_value)
                        todayRate.text = rate.getString("price") + " LKR"

                        val Remaining: TextView = findViewById(R.id.widget02_value)
                        Remaining.text = count

                        val todayList = jsonObject.getJSONArray("today")
                        val upcomingList = jsonObject.getJSONArray("upcoming")

                        for (i in 0 until todayList.length()) {
                            val item = todayList.getJSONObject(i)

                            val id = item.getInt("id")
                            val area = item.getString("area")
                            val time = methods.convertTime(item.getString("time"))
                            val amount = item.getInt("amount")
                            val method = item.getString("payment_method")

                            todayCollectionItems.add(CollectionItem(id, area, time, amount, method))
                        }

                        for (i in 0 until upcomingList.length()) {
                            val item = upcomingList.getJSONObject(i)

                            val id = item.getInt("id")
                            val area = item.getString("area")
                            val time = methods.convertTime(item.getString("time"))
                            val amount = item.getInt("amount")
                            val method = item.getString("payment_method")

                            upcomingCollectionItems.add(
                                CollectionItem(
                                    id,
                                    area,
                                    time,
                                    amount,
                                    method
                                )
                            )
                        }
                    }
                } else if (response.code() == 202) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val jsonString = it.string() // Convert response body to JSON string
                        val jsonObject = JSONObject(jsonString)
                        val size = jsonObject.optInt("size")
                        val count = jsonObject.optString("count")
                        val rate = jsonObject.getJSONObject("rate")

                        if (size == -2) {
                            // no today and upcoming collections
                        } else if (size == -1) {
                            // no today
                            val upcomingList = jsonObject.getJSONArray("upcoming")

                            for (i in 0 until upcomingList.length()) {
                                val item = upcomingList.getJSONObject(i)

                                val id = item.getInt("id")
                                val area = item.getString("area")
                                val time = methods.convertTime(item.getString("time"))
                                val amount = item.getInt("amount")
                                val method = item.getString("payment_method")

                                upcomingCollectionItems.add(
                                    CollectionItem(
                                        id,
                                        area,
                                        time,
                                        amount,
                                        method
                                    )
                                )
                            }
                        } else {
                            // no upcoming
                            val todayList = jsonObject.getJSONArray("today")

                            for (i in 0 until todayList.length()) {
                                val item = todayList.getJSONObject(i)

                                val id = item.getInt("id")
                                val area = item.getString("area")
                                val time = methods.convertTime(item.getString("time"))
                                val amount = item.getInt("amount")
                                val method = item.getString("payment_method")

                                todayCollectionItems.add(
                                    CollectionItem(
                                        id,
                                        area,
                                        time,
                                        amount,
                                        method
                                    )
                                )
                            }
                        }

                        val todayRate: TextView = findViewById(R.id.widget01_value)
                        todayRate.text = rate.getString("price") + " LKR"

                        val Remaining: TextView = findViewById(R.id.widget02_value)
                        Remaining.text = count
                    }
                } else if (response.code() == 401) {
                    // unauthorized
                } else {
                    Log.d("TAG", "Went wrong")
                    Log.d("TAG", response.code().toString())
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                // Handle failure
                Log.d("TAG", "An error occurred: $t")
            }
        })

        // after fetch all data
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // set collector's name on dashboard
        var collectorName: TextView = findViewById(R.id.collectorName)
        sharedPreferences = getSharedPreferences("login_pref", Context.MODE_PRIVATE)
        collectorName.text = sharedPreferences.getString("name", "Collector")

        // set greeting msg
        var greeting: TextView = findViewById(R.id.greeting)
        val currentTime = LocalTime.now()
        greeting.text = methods.getGreetingTime(currentTime)

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
}