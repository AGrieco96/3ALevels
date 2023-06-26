package com.A3Levels.game

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import com.A3Levels.auth.GoogleSignInActivity
import com.A3Levels.databinding.ActivityStrongboxLevelBinding
import com.A3Levels.databinding.ActivityTestLevelBinding
import com.A3Levels.other.RequestsHTTP
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class StrongboxLevelActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStrongboxLevelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStrongboxLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCompass()
    }

    private fun startCompass(){


    }

}