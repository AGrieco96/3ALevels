package com.A3Levels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val intent = Intent(this, LaMiaPrimaVolta::class.java)
        startActivity(intent)

        val buttonClick = findViewById<Button>(R.id.firstButton)
        buttonClick.setOnClickListener {
            val intent = Intent(this, LaMiaPrimaVolta::class.java)
            startActivity(intent)
        }

        val button2Click = findViewById<Button>(R.id.secondButton)
        button2Click.setOnClickListener {
            val intent2 = Intent(this, SecondActivity::class.java)
            startActivity(intent2)
        }

    }
}