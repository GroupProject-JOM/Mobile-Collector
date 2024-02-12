package org.jom.collector.collections

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jom.collector.AddCookiesInterceptor
import org.jom.collector.CollectionItem
import org.jom.collector.DashboardActivity
import org.jom.collector.DashboardApi
import org.jom.collector.Methods
import org.jom.collector.R
import org.jom.collector.profile.ViewProfileActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CollectionApi {
    @GET("JOM_war_exploded/pickup-collection")
    fun getData(
        @Query("id") id: Int,
    ): Call<ResponseBody>
}

class ViewCollectionActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var backButton: ImageView
    private lateinit var complete: Button
    private lateinit var jwt: String

    // get instance of methods class
    val methods = Methods()

    // get bundle instance for send data for next intent
    var extras = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {// get cookie operations
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

        val collectionApi = retrofit.create(CollectionApi::class.java)

        // Retrieve the Intent that started this activity and get the value of the "id" extra
        val intent = intent
        val id = intent.getIntExtra("id", 0)

        // call get data function to get data from backend
        collectionApi.getData(id).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val jsonString = it.string() // Convert response body to JSON string
                        val jsonObject = JSONObject(jsonString)
                        val collection = jsonObject.getJSONObject("collection")

                        // initialize text views
                        val page_title: TextView = findViewById(R.id.page_title)
                        val supplier_name: TextView = findViewById(R.id.supplier_name)
                        val supplier_phone: TextView = findViewById(R.id.supplier_phone)
                        val estate: TextView = findViewById(R.id.estate)
                        val date: TextView = findViewById(R.id.date)
                        val time: TextView = findViewById(R.id.time)
                        val count: TextView = findViewById(R.id.count)
                        val payment: TextView = findViewById(R.id.payment)

                        // assign values to text views
                        page_title.text = "Collection ID ${collection.getString("id")}"
                        supplier_name.text =
                            "${collection.getString("name")} ${collection.getString("last_name")}"
                        supplier_phone.text = collection.getString("phone")
                        estate.text = collection.getString("address")
                        date.text = collection.getString("date")
                        time.text = methods.convertTime(collection.getString("time"))
                        count.text = methods.formatAmount(collection.getString("amount").toDouble())
                        payment.text = collection.getString("payment_method").capitalize()

                        var location = collection.getString("location")

                        // add data to bundle to send to next intent
                        extras.putString("id", collection.getString("id"))
                        extras.putString("name", "${collection.getString("name")} ${collection.getString("last_name")}")
                        extras.putString("phone", collection.getString("phone"))
                        extras.putString("address", collection.getString("address"))
                        extras.putString("date", collection.getString("date"))
                        extras.putString("time", methods.convertTime(collection.getString("time")))
                        extras.putString("amount", methods.formatAmount(collection.getString("amount").toDouble()))
                        extras.putString("payment", collection.getString("payment_method").capitalize())
                    }
                } else if (response.code() == 202) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val jsonString = it.string() // Convert response body to JSON string
                        val jsonObject = JSONObject(jsonString)
                        val collection = jsonObject.optString("collection")

                        println(collection)
                        Log.d("TAG", collection)
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
        setContentView(R.layout.activity_view_collection)

        //back
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { this.onBackPressed() }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)

        // complete collection
        complete = findViewById(R.id.complete)
        complete.setOnClickListener {
            val intent = Intent(this, CompleteCollectionActivity::class.java)
            intent.putExtras(extras)
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
            };
        }
    }
}