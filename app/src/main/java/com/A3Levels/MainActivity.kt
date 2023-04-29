package com.A3Levels

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.auth.LoginEmailActivity
import com.A3Levels.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.auth.signOut()
        setContentView(R.layout.activity_main)
    }

    val mainHandler = Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(this@MainActivity, LoginEmailActivity::class.java)
        startActivity(intent)
        finish()
    },3000)



}