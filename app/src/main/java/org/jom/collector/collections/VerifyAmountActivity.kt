package org.jom.collector.collections

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jom.collector.DashboardActivity
import org.jom.collector.R
import org.jom.collector.profile.ViewProfileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.jom.collector.AddCookiesInterceptor
import org.jom.collector.Methods
import org.jom.collector.SigninApi
import org.jom.collector.signInFormData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import java.util.concurrent.atomic.AtomicBoolean

data class CompleteCollectionFormData(
    val id: Int,
    val amount: Int,
)

data class VerifyAmountFormData(
    val otp: Int,
    val oId: Int,
)

interface CompleteCollectionApi {
    @PUT("JOM_war_exploded/pickup-collection")
    fun completeCollection(@Body formData: CompleteCollectionFormData): Call<ResponseBody>
}

interface OptionalVerificationApi {
    @POST("JOM_war_exploded/optional-verification")
    fun optionalVerification(@Body formData: CompleteCollectionFormData): Call<ResponseBody>
}

interface VerifyAmountApi {
    @POST("JOM_war_exploded/verify-amount")
    fun verifyAmountFormData(@Body formData: VerifyAmountFormData): Call<ResponseBody>
}

class VerifyAmountActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var backButton: ImageView
    private lateinit var optional: Button
    private lateinit var verify: Button
    private lateinit var defaultLayout: ConstraintLayout
    private lateinit var optionalLayout: ConstraintLayout
    private lateinit var otp: EditText
    private lateinit var otpError: TextView
    private lateinit var jwt: String

    // get instance of methods class
    val methods = Methods()

    // status variables for validations
    var otp_status = false
    var send_status = false

    var otpId = 0

    // get bundle instance for send data for next intent
    var extras = Bundle()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_amount)

        // initialize layouts and their visibilities
        defaultLayout = findViewById(R.id.default_layout)
        optionalLayout = findViewById(R.id.optional_layout)
        defaultLayout.visibility = View.VISIBLE
        optionalLayout.visibility = View.GONE

        // initialize text views
        val collection_id: TextView = findViewById(R.id.collection_id)
        val name: TextView = findViewById(R.id.supplier_name)
        val phone: TextView = findViewById(R.id.phone)
        val amount: TextView = findViewById(R.id.count)
        val actual: TextView = findViewById(R.id.actual)

        // get intent instance and bundle
        var i = intent
        extras = i.extras!!

        // get put data from bundle and assign to text views
        collection_id.text = extras.getString("id")
        name.text = extras.getString("name")
        phone.text = extras.getString("phone")
        amount.text = extras.getString("amount")
        actual.text = extras.getString("actual")

        // get cookie operations
        val cookieManager = CookieManager.getInstance()
        val cookies = methods.getAllCookies(cookieManager)

        // get jwt from cookie
        for (cookie in cookies) {
            if (cookie.first == "jwt") {
                jwt = cookie.second
            }
        }

        var payload = methods.getPayload(jwt);

        // assign values to socket operation variables
        val senderId = methods.floatToInt(payload["user"])
        val socket_amount = extras.getString("actual").toString().toInt()
        val collectionId = methods.floatToInt(extras.getString("id"))


        runBlocking {
            val socket = OkHttpClient().newWebSocket(
                Request.Builder()
                    .url("ws://10.0.2.2:8090/JOM_war_exploded/verify-amount/${senderId}")
                    .build(),
                object : WebSocketListener() {
                    private val isConnected = AtomicBoolean(false)

                    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                        println("WebSocket opened: $response")
                        isConnected.set(true)
                        CoroutineScope(Dispatchers.IO).launch {
                            webSocket.send("${senderId}:${socket_amount}:${collectionId}")
                            println("LOG ${senderId}:${socket_amount}:${collectionId}")
                        }
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        if (text.isNotEmpty()) {
                            val arr = text.split(":")
                            val msg = arr[0]
                            val id = arr[1].toInt()

                            // action Verify Decline
                            actionVerifyDecline(id, msg, collectionId, socket_amount)
                        }
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        println("WebSocket closed: $code $reason")
                        isConnected.set(false)
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: okhttp3.Response?
                    ) {
                        println("WebSocket error: $t")
                        isConnected.set(false)
                    }
                }
            )
        }

        // get otp input and error text
        otp = findViewById(R.id.otp)
        otpError = findViewById(R.id.otpError)

        // Error handling
        fun otp_status_func(otp: String): Boolean {
            val trimmedOTP = otp.trim()
            if (trimmedOTP.isEmpty()) {
                otpError.text = "OTP cannot be empty"
                otp_status = false
                return false
            } else if (trimmedOTP.toInt() < 1) {
                otpError.text = "OTP must be numbers"
                otp_status = false
                return false
            } else {
                otpError.text = ""
                otp_status = true
                return true
            }
        }

        // handle onInput change errors
        val otpTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                otp_status_func(s.toString())
            }
        }
        otp.addTextChangedListener(otpTextWatcher)


        //back
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { this.onBackPressed() }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)

        // optional verification
        optional = findViewById(R.id.optional)
        optional.setOnClickListener {
            defaultLayout.visibility = View.GONE
            optionalLayout.visibility = View.VISIBLE
            otpError.text = "Please wait OTP is sending to supplier's Email"

            // send otp to email
            optionalVerification(collectionId, socket_amount)
        }

        // verify otp
        verify = findViewById(R.id.verify)
        verify.setOnClickListener {
            otp_status_func(otp.text.toString())
            if (otp_status) {
                val intent = Intent(this, CollectionCompletedActivity::class.java)
                VerifyOTP(intent, collectionId, socket_amount)
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

    fun actionVerifyDecline(id: Int, msg: String, collectionId: Int, amount: Int) {
        runOnUiThread {
            if (collectionId == id) {
                if (msg == "OK") {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Completed!")
                        .setContentText("The supplier confirmed that the amount of coconut entered was correct.")
                        .setConfirmText("Ok")
                        .setConfirmClickListener {
                            // complete collection with supplier's ok confirmation
                            val intent = Intent(this, CollectionCompletedActivity::class.java)
                            CompleteRequest(id, amount, intent)
                        }.show()
                } else if (msg == "Denied") {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Incorrect!")
                        .setContentText("The supplier denied the entered coconut quantity.")
                        .setConfirmText("Enter again")
                        .setConfirmClickListener {
                            // back to enter amount with supplier's denied msg
                            val intent = Intent(this, CompleteCollectionActivity::class.java)
                            intent.putExtras(extras)
                            startActivity(intent)
                            finish()
                        }.show()
                }
            }
        }
    }

    fun CompleteRequest(id: Int, amount: Int, intent: Intent) {
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

        val completeCollectionApi = retrofit.create(CompleteCollectionApi::class.java)

        val formData = CompleteCollectionFormData(
            id = id,
            amount = amount,
        )

        completeCollectionApi.completeCollection(formData)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        intent.putExtras(extras)
                        startActivity(intent)
                        finish()
                    } else if (response.code() == 409) {
                        Log.d("TAG", "Went wrong")
                        Log.d("TAG", response.code().toString())
                    } else if (response.code() == 401) {
                        Log.d("TAG", "Unauthorized")
                        Log.d("TAG", response.code().toString())
                    } else {
                        // Handle error
                        Log.d("TAG", response.code().toString())
                        val responseBody = response.body()
                        responseBody?.let {
                            val jsonString = it.string() // Convert response body to JSON string
                            val jsonObject =
                                JSONObject(jsonString) // Convert JSON string to JSONObject
                            val message =
                                jsonObject.optString("message") // Extract message field from JSON
                            Log.d("TAG", message)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle failure
                    Log.d("TAG", "An error occurred: $t")
                }
            })
    }

    fun optionalVerification(id: Int, amount: Int) {
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

        val optionalVerificationApi = retrofit.create(OptionalVerificationApi::class.java)

        val formData = CompleteCollectionFormData(
            id = id,
            amount = amount,
        )

        optionalVerificationApi.optionalVerification(formData)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        val responseBody = response.body()
                        responseBody?.let {
                            val jsonString = it.string() // Convert response body to JSON string
                            val jsonObject =
                                JSONObject(jsonString) // Convert JSON string to JSONObject
                            otpId = jsonObject.optInt("oId")
                        }

                        otpError.text = "OTP Sent"
                        send_status = true
                        otp.isEnabled = true
                    } else if (response.code() == 409) {
                        otpError.text = "Failed to send OTP, please try again later"
                        Log.d("TAG", "Failed to send OTP, please try again later")
                        Log.d("TAG", response.code().toString())
                        send_status = false
                        otp.isEnabled = false
                    } else if (response.code() == 401) {
                        Log.d("TAG", "Unauthorized")
                        Log.d("TAG", response.code().toString())
                        send_status = false
                        otp.isEnabled = false
                    } else {
                        // Handle error
                        Log.d("TAG", response.code().toString())
                        val responseBody = response.body()
                        responseBody?.let {
                            val jsonString = it.string() // Convert response body to JSON string
                            val jsonObject =
                                JSONObject(jsonString) // Convert JSON string to JSONObject
                            val message =
                                jsonObject.optString("message") // Extract message field from JSON
                            Log.d("TAG", message)
                        }
                        send_status = false
                        otp.isEnabled = false
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle failure
                    Log.d("TAG", "An error occurred: $t")
                    send_status = false
                    otp.isEnabled = false
                }
            })
    }

    fun VerifyOTP(intent: Intent, id: Int, amount: Int) {
        runOnUiThread {
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

            val verifyAmountApi = retrofit.create(VerifyAmountApi::class.java)

            val formData = VerifyAmountFormData(
                otp = otp.text.toString().toInt(),
                oId = otpId,
            )

            verifyAmountApi.verifyAmountFormData(formData)
                .enqueue(object : retrofit2.Callback<ResponseBody> {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: retrofit2.Response<ResponseBody>
                    ) {
                        if (!isFinishing && !isDestroyed) {
                            if (response.code() == 200) {
                                // Show dialog only if activity is running
                                SweetAlertDialog(
                                    this@VerifyAmountActivity,
                                    SweetAlertDialog.SUCCESS_TYPE
                                )
                                    .setTitleText("Completed!")
                                    .setContentText("The supplier confirmed that the amount of coconut entered was correct.")
                                    .setConfirmText("Ok")
                                    .setConfirmClickListener {
                                        CompleteRequest(id, amount, intent)
                                    }.show()
                            } else if (response.code() == 401) {
                                if (!isFinishing && !isDestroyed) {
                                    otpError.text = "Invalid OTP"
                                    Log.d("TAG", "Unauthorized/Invalid OTP")
                                    Log.d("TAG", response.code().toString())
                                }
                            } else {
                                if (!isFinishing && !isDestroyed) {
                                    // Handle error
                                    Log.d("TAG", response.code().toString())
                                    val responseBody = response.body()
                                    responseBody?.let {
                                        val jsonString =
                                            it.string() // Convert response body to JSON string
                                        val jsonObject =
                                            JSONObject(jsonString) // Convert JSON string to JSONObject
                                        val message =
                                            jsonObject.optString("message") // Extract message field from JSON
                                        Log.d("TAG", message)
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        // Handle failure
                        Log.d("TAG", "An error occurred: $t")
                    }
                })
        }
    }
}