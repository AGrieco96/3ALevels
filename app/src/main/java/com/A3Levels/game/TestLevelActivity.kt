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
import com.A3Levels.databinding.ActivityTestLevelBinding
import com.A3Levels.other.RequestsHTTP
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class TestLevelActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTestLevelBinding
    private lateinit var lobbyId : String
    private var livello : String = ""

    /*
        Global Handler Timer
     */
    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0
    private var deciseconds = 0
    private var milliseconds = 0
    private val updateTimer = object : Runnable {
        override fun run() {
            milliseconds += 10
            if (milliseconds == 1000) {
                seconds++
                milliseconds = 0
            }
            if (milliseconds % 100 == 0) {
                deciseconds++
            }
            binding.timerTextView.text = String.format("%02d:%02d.%02d", seconds / 60, seconds % 60, milliseconds / 10)
            handler.postDelayed(this, 10)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lobbyId = intent.getStringExtra("lobbyId").toString()
        println("LobbyID nel TestLevel " + lobbyId)

        // Start animation to leave tutorial and display game UI.
        set_game_UI()

        //Testing purpose.
        binding.buttonEnd.setOnClickListener{
            end_game()
        }


        /* Forse bisogna aggiungere un campo nel db che incrementa con l'incrementare dei livelli ??? */

    }

    private fun set_game_UI(){
        // Create a AlphaAnimation object
        val animation = AlphaAnimation(1.0f,0.0f)
        // Set the animation properties
        animation.duration = 6000
        animation.fillAfter = true
        animation.interpolator = LinearInterpolator()

        // Set the animation listener
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                binding.layoutTutorial.visibility = View.VISIBLE
                binding.layoutEnd.visibility = View.GONE
                binding.layoutGame.visibility = View.INVISIBLE
                binding.buttonEnd.visibility = View.GONE
                binding.timerTextView.visibility = View.INVISIBLE
            }
            override fun onAnimationEnd(animation: Animation) {
                // Do something when the animation ends
                binding.layoutTutorial.visibility = View.GONE
                binding.layoutGame.visibility = View.VISIBLE
                binding.buttonEnd.visibility = View.VISIBLE
                binding.timerTextView.visibility = View.VISIBLE
                startTimer()
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.layoutTutorial.startAnimation(animation)

    }

    private fun end_game(){
        // Retrieve level information from firestore
        /*
        FirebaseApp.initializeApp(this)
        val db = FirebaseFirestore.getInstance()
        val gamesRef = db.collection("games").document(lobbyId)
        gamesRef.get()
            .addOnCompleteListener --> Rende la chiamata asincrona.
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    livello = documentSnapshot.get("level").toString()
                    println("Livello documento " + livello )
                } else {
                    Log.d(TAG, "Il documento non esiste")
                }
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error getting level ID: $exception")
            }
        */
        binding.timerTextView2.text = String.format("%02d:%02d.%02d", seconds / 60, seconds % 60, milliseconds / 10)
        var finalTime = ((seconds*1000)+milliseconds).toString()

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("lobby_id", lobbyId)
        jsonObject.put("player_id", intent.getStringExtra("username"))
        //jsonObject.put("level", livello)
        jsonObject.put("time", finalTime)
        RequestsHTTP.httpPOSTGameUpdate(jsonObject)

        stopTimer()
        // Setup UI for the ending of the level.
        binding.layoutTutorial.visibility = View.GONE
        binding.layoutEnd.visibility = View.VISIBLE
        binding.layoutGame.visibility = View.GONE
        binding.buttonEnd.visibility = View.INVISIBLE
        binding.timerTextView.visibility = View.INVISIBLE

    }

    private fun startTimer() {
        handler.post(updateTimer)
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateTimer)
        seconds = 0
        deciseconds = 0
        milliseconds = 0
    }

}