package com.A3Levels.other

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.A3Levels.HomeActivity
import com.A3Levels.R
import com.A3Levels.databinding.ActivityCreditsBinding
import com.A3Levels.databinding.ActivityOptionBinding

class CreditsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start the animation
        startAnimation()
    }

    private fun startAnimation() {
        // Create a TranslateAnimation object
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f,
            Animation.RELATIVE_TO_SELF, 0f
        )

        // Set the animation properties
        animation.duration = 10000
        animation.fillAfter = true
        animation.interpolator = LinearInterpolator()

        // Set the animation listener
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // Finish the activity when the animation ends
                // finish()
                binding.buttonBack.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        // Start the animation on the credits text view
        binding.creditText.startAnimation(animation)

        binding.buttonBack.setOnClickListener{
            goBack()
        }
    }

    private fun goBack(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}