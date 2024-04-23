package org.jom.collector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jom.collector.profile.ViewProfileActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.POST

interface CompletedApi {
    @DELETE("api/collector")
    fun getData(): Call<ResponseBody>
}

class CompletedCollectionsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recycleViewPast: RecyclerView
    private lateinit var pastCollectionsAdapter: CollectionsAdapter
    private lateinit var jwt: String

    val pastCollectionItems = mutableListOf<CollectionItem>()

    // get instance of methods class
    val methods = Methods()
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
            .baseUrl(methods.getBackendUrl())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val completedApi = retrofit.create(CompletedApi::class.java)

        // call get data function to get data from backend
        completedApi.getData().enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val jsonString = it.string() // Convert response body to JSON string
                        val jsonObject = JSONObject(jsonString)
                        val pastList = jsonObject.getJSONArray("list")

                        for (i in 0 until pastList.length()) {
                            val item = pastList.getJSONObject(i)

                            val id = item.getInt("id")
                            val area = item.getString("area")
                            val time = item.getString("date")
                            val amount = item.getInt("amount")
                            val method = item.getString("payment_method")

                            pastCollectionItems.add(
                                CollectionItem(
                                    id,
                                    area,
                                    time,
                                    amount,
                                    method
                                )
                            )

                            // Set up RecyclerView and its adapter after data is fetched
                            runOnUiThread {
                                pastCollectionsAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                } else if (response.code() == 202) {
                    Log.d("TAG", "No past collections")
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

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_collections)

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)

        // recycle views handle
        recycleViewPast = findViewById(R.id.completed_recycle)
        recycleViewPast.layoutManager = LinearLayoutManager(this)
        pastCollectionsAdapter = CollectionsAdapter(pastCollectionItems)
        recycleViewPast.adapter = pastCollectionsAdapter


        Log.d("TAG", recycleViewPast.toString())

        // bottom nav handler
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_completed
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
            }
        }
    }
}