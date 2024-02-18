package org.jom.collector.profile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jom.collector.AddCookiesInterceptor
import org.jom.collector.DashboardActivity
import org.jom.collector.Methods
import org.jom.collector.R
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ProfileApi {
    @GET("JOM_war_exploded/profile")
    fun getData(): Call<ResponseBody>
}

class ViewProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var backButton: ImageView
    private lateinit var edit: Button
    private lateinit var jwt: String

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
            .baseUrl("http://10.0.2.2:8090/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val profileApi = retrofit.create(ProfileApi::class.java)

        // call get data function to get data from backend
        profileApi.getData().enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val jsonString = it.string() // Convert response body to JSON string
                        val jsonObject = JSONObject(jsonString)
                        val user = jsonObject.getJSONObject("user")

                        // initialize text views
                        val first_name: TextView = findViewById(R.id.first_name)
                        val last_name: TextView = findViewById(R.id.last_name)
                        val email: TextView = findViewById(R.id.email)
                        val contact: TextView = findViewById(R.id.contact)
                        val nic: TextView = findViewById(R.id.nic)
                        val dob: TextView = findViewById(R.id.dob)
                        val gender: TextView = findViewById(R.id.gender)
                        val address_1: TextView = findViewById(R.id.address_1)
                        val address_2: TextView = findViewById(R.id.address_2)
                        val address_3: TextView = findViewById(R.id.address_3)
                        val role: TextView = findViewById(R.id.role)

                        // assign values to text views
                        first_name.text=user.getString("first_name")
                        last_name.text=user.getString("last_name")
                        email.text=user.getString("email")
                        contact.text=user.getString("phone")
                        nic.text=user.getString("nic")
                        gender.text=user.getString("gender")
                        address_1.text=user.getString("add_line_1")
                        address_2.text=user.getString("add_line_2")
                        address_3.text=user.getString("add_line_3")
                        role.text=user.getString("role").capitalize()
                    }
                } else if (response.code() == 400) {
                    Log.d("TAG", "No user")
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
        setContentView(R.layout.activity_view_profile)

        //back
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { this.onBackPressed() }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)

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
