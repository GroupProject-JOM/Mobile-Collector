package org.jom.collector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class signInFormData(
    val username: String,
    val password: String,
)

interface SigninApi {
    @POST("JOM_war_exploded/signin")
    fun signin(@Body formData: signInFormData): Call<ResponseBody>
}


class LoginActivity : AppCompatActivity() {

    lateinit private var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val intent = Intent(this, DashboardActivity::class.java)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.darkBoxShadow)

        loginBtn = findViewById(R.id.login)

        loginBtn.setOnClickListener {

            val username: EditText = findViewById(R.id.username_input)
            val password: EditText = findViewById(R.id.password_input)

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8090/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val signinApi = retrofit.create(SigninApi::class.java)

            val formData = signInFormData(
                username = username.text.toString(),
                password = password.text.toString(),
            )

            signinApi.signin(formData).enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    if (response.code() == 200) {

                        val responseBody = response.body()
                        responseBody?.let {
                            val jsonString = it.string() // Convert response body to JSON string
                            val jsonObject =
                                JSONObject(jsonString) // Convert JSON string to JSONObject
                            val message =
                                jsonObject.optString("message") // Extract message field from JSON
                            val jwt = jsonObject.optString("jwt")
                            Log.d("TAG", message)

                            val cookieManager = CookieManager.getInstance()
                            val cookieName = "jwt"
                            val cookieValue = jwt
                            val domain = "10.0.2.2"
                            val path = "/"

                            // Create a cookie string
                            val cookieString =
                                "$cookieName=$cookieValue; domain=$domain; path=$path"

                            // Add the cookie to the CookieManager
                            cookieManager.setCookie(domain, cookieString)
                        }
                        startActivity(intent)
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
    }
}