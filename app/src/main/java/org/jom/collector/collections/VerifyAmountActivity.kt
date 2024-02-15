package org.jom.collector.collections

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.jom.collector.Methods
import java.util.concurrent.atomic.AtomicBoolean

class VerifyAmountActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var backButton: ImageView
    private lateinit var optional: Button
    private lateinit var jwt: String

    // get instance of methods class
    val methods = Methods()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_amount)

        // initialize text views
        val collection_id: TextView = findViewById(R.id.collection_id)
        val name: TextView = findViewById(R.id.supplier_name)
        val phone: TextView = findViewById(R.id.phone)
        val amount: TextView = findViewById(R.id.count)
        val actual: TextView = findViewById(R.id.actual)

        // get intent instance and bundle
        var i = intent
        var extras = i.extras!!

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

        val senderId = methods.floatToInt(payload["user"])
        val socket_amount = extras.getString("actual")
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
                            actionVerifyDecline(id, msg, collectionId)
                            println(text)
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

//            while (isConnected.get()) {
//                // wait for messages
//            }
        }


        //back
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { this.onBackPressed() }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)

        // optional verification
        optional = findViewById(R.id.optional)
        optional.setOnClickListener {
            val intent = Intent(this, OptionalVerificationActivity::class.java)
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

    fun actionVerifyDecline(id: Int, msg: String, collectionId: Int) {
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
                            startActivity(intent)
                            finish()
                        }.show()
                } else if (msg == "Denied") {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Incorrect!")
                        .setContentText("The supplier denied the entered coconut quantity.")
                        .setConfirmText("Enter again")
                        .setConfirmClickListener {
                            // back to enter amount with supplier's denied msg
                            val intent = Intent(this, CompleteCollectionActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.show()
                }
            }
        }
    }
}