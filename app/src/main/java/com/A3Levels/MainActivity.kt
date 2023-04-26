package com.A3Levels

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.auth.RegisterEmailActivity
import com.A3Levels.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.authText.setOnClickListener {
            goToRegister()
        }
    }

    private fun goToRegister(){
        val intent = Intent(this, RegisterEmailActivity::class.java)
        startActivity(intent)
    }
}