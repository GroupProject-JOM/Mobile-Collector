package org.jom.collector

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {


    private lateinit var logo: ImageView
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //back
        logo = findViewById(R.id.JomLogo)
        logo.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightBodyColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.darkBoxShadow)

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(this,LoginActivity :: class.java )
            startActivity(intent)
        },3000.toLong())

    }
}