package com.A3Levels

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.auth.LoginEmailActivity
import com.A3Levels.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.A3Levels.graphics.PyramidActivity

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.A3Levels.game.LobbyActivity
import com.A3Levels.game.gameLevelExtraInfo


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.auth.signOut()
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        startAnimation()
    }
    /*
    val mainHandler = Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(this@MainActivity, LoginEmailActivity::class.java)
        startActivity(intent)
        //finish()
    },3000)
    */


    private fun startAnimation() {
        // Create a TranslateAnimation object
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -1f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )

        // Set the animation properties
        animation.duration = 2000
        animation.fillAfter = true
        animation.interpolator = LinearInterpolator()

        // Set the animation listener
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // Finish the activity when the animation ends
                // finish()
                val intent = Intent(this@MainActivity, LoginEmailActivity::class.java)
                //gameLevelExtraInfo.setUsername("Nardoz33")
                //gameLevelExtraInfo.setLobbyId("7")
                //gameLevelExtraInfo.setUsername("testMultiplayer")
                //intent.putExtra("level", 1)
                //intent.putExtra("flag",true)
                startActivity(intent)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        // Start the animation on the credits text view
        binding.logoText.startAnimation(animation)

    }



}