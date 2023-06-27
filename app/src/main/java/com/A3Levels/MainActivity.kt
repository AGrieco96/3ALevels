package com.A3Levels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.A3Levels.game.SquareLevelActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val intent = Intent(this, SquareLevelActivity::class.java)
        startActivity(intent)

    }
}