package org.jom.collector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    lateinit private var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.darkBoxShadow)

        loginBtn = findViewById(R.id.login)

        loginBtn.setOnClickListener{
            Toast.makeText(applicationContext,"Login successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}