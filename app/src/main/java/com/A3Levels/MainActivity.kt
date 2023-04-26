package com.A3Levels

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.auth.LoginEmailActivity
import com.A3Levels.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Firebase.auth.signOut()

        binding.authText.setOnClickListener {
            goToRegister()
        }
    }

    private fun goToRegister(){
        val intent = Intent(this, LoginEmailActivity::class.java)
        startActivity(intent)
    }
}