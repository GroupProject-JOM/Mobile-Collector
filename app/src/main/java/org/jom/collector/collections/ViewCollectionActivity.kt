package org.jom.collector.collections

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jom.collector.AddCookiesInterceptor
import org.jom.collector.AssignCollectionsActivity
import org.jom.collector.CollectionItem
import org.jom.collector.CompletedCollectionsActivity
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
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

interface CollectionApi {
    @GET("api/pickup-collection")
    fun getData(
        @Query("id") id: Int,
    ): Call<ResponseBody>
}

class ViewCollectionActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var backButton: ImageView
    private lateinit var complete: Button
    private lateinit var direction: Button
    private lateinit var jwt: String
    private lateinit var location: String

    private lateinit var mapView: MapView
    private lateinit var locationManager: LocationManager

    private val REQUEST_LOCATION_CODE = 100

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
            .baseUrl(methods.getBackendUrl())
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
                        val fCount: TextView = findViewById(R.id.final_count)
                        val fCountText: TextView = findViewById(R.id.final_count_text)
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

                        location = collection.getString("location")

                        // add data to bundle to send to next intent
                        extras.putString("id", collection.getString("id"))
                        extras.putString(
                            "name",
                            "${collection.getString("name")} ${collection.getString("last_name")}"
                        )
                        extras.putString("phone", collection.getString("phone"))
                        extras.putString("address", collection.getString("address"))
                        extras.putString("date", collection.getString("date"))
                        extras.putString("time", methods.convertTime(collection.getString("time")))
                        extras.putString(
                            "amount",
                            methods.formatAmount(collection.getString("amount").toDouble())
                        )
                        extras.putString(
                            "payment",
                            collection.getString("payment_method").capitalize()
                        )

                        if (collection.getInt("status") != 3) {
                            complete.visibility = View.GONE
                            direction.visibility = View.GONE
                            fCount.visibility = View.VISIBLE
                            fCountText.visibility = View.VISIBLE

                            val dateText: TextView = findViewById(R.id.date_text)
                            val timeText: TextView = findViewById(R.id.time_text)

                            dateText.text = "Collected Date"
                            timeText.text = "Collected Time"

                            date.text = collection.getString("collected_date")
                            time.text = methods.convertTime(collection.getString("collected_time"))
                            fCount.text = methods.formatAmount(
                                collection.getString("final_amount").toDouble()
                            )
                        }
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


        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState) // Important for lifecycle management

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check and request location permission (important for Android 6.0+)
        if (checkLocationPermission()) {
            // Location permission granted, configure map to show user location
            configureMapView()
        } else {
            requestLocationPermission()
        }


        //back
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { this.onBackPressed() }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)

        // complete collection
        complete = findViewById(R.id.complete)
        complete.setOnClickListener {
            val intent = Intent(this, CompleteCollectionActivity::class.java)
            intent.putExtras(extras)
            startActivity(intent)
        }

        // get direction
        direction = findViewById(R.id.direction)
        direction.setOnClickListener {
            // Split the location string into latitude and longitude
            val locationSplit = location.split(" ")
            val latitude = locationSplit[0].toDouble()
            val longitude = locationSplit[1].toDouble()

//            val uri = "http://maps.google.com/maps?saddr=Current+Location&daddr=${latitude},${longitude}"
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//            intent.setPackage("com.google.android.apps.maps")
//            startActivity(intent)

            if (checkLocationPermission()) {
                // Request location updates
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        val currentLatitude = location.latitude
                        val currentLongitude = location.longitude
                        val uri = "http://maps.google.com/maps?saddr=$currentLatitude,$currentLongitude&daddr=${latitude},${longitude}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        intent.setPackage("com.google.android.apps.maps")
                        startActivity(intent)
                    }

                    override fun onProviderDisabled(provider: String) {
                        // Handle provider disabled
                    }

                    override fun onProviderEnabled(provider: String) {
                        // Handle provider enabled
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
                        // Handle location status changes
                    }
                }, null)
            } else {
                // Request location permission if not granted
                requestLocationPermission()
            }
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

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun checkLocationPermission(): Boolean {
        // Check for permissions (adjust based on your target SDK version)
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        // Request permissions (adjust based on your target SDK version)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE) {
            configureMapView()
        }
    }

    private fun configureMapView() {
        mapView.getMapAsync { map ->
            // Split the location string into latitude and longitude
            val locationSplit = location.split(" ")
            val latitude = locationSplit[0].toDouble()
            val longitude = locationSplit[1].toDouble()

            // Create LatLng object for the location
            val collectionLocation = LatLng(latitude, longitude)

            // Move camera to the collection location
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    collectionLocation,
                    15f
                )
            ) // Adjust zoom level as needed

            // Add a marker at the collection location
            map.addMarker(MarkerOptions().position(collectionLocation).title("Collection Location"))
        }
    }


}