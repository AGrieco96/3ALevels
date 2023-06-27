package com.A3Levels.game

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.A3Levels.R
import com.A3Levels.databinding.ActivityTestLevelBinding

class GameLevelActivity : AppCompatActivity(){
    private lateinit var binding: ActivityTestLevelBinding
    private lateinit var lobbyId : String
    private lateinit var username : String
    private var counterLevel = intent.getStringExtra("level").toString().toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestLevelBinding.inflate(layoutInflater)
        //lobbyId = intent.getStringExtra("lobbyId").toString()
        //username = intent.getStringExtra("username").toString()

        set_game_UI()
    }

    /*
    private fun startTimer() {
        handler.post(updateTimer)
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateTimer)
        seconds = 0
        deciseconds = 0
        milliseconds = 0
    }
    */
    fun set_game_UI(){
        setPersonalInfoLevelUI()
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
                //binding.buttonEnd.visibility = View.GONE
                binding.timerTextView.visibility = View.INVISIBLE
            }
            override fun onAnimationEnd(animation: Animation) {
                // Do something when the animation ends
                binding.layoutTutorial.visibility = View.GONE
                //binding.layoutGame.visibility = View.VISIBLE
                //binding.buttonEnd.visibility = View.VISIBLE
                //binding.timerTextView.visibility = View.VISIBLE
                //gameLevels[counterTest].startTimer(this@GameLevelActivity )
                startLevel()
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.layoutTutorial.startAnimation(animation)
    }

    fun setPersonalInfoLevelUI(){

        when(counterLevel){
            1 -> {
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_1)
            }
            2 -> {
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_2)
            }
            3-> {
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_3)
            }
        }
    }

    fun startLevel(){
        when(counterLevel){
            1 ->
        }
        //Main execution of the logic.
        val intent = Intent(this, LevelPhotoActivity::class.java)
        intent.putExtra("lobbyId", lobbyId)
        intent.putExtra("username", username)
        startActivity(intent)
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
        //binding.timerTextView2.text = String.format("%02d:%02d.%02d", seconds / 60, seconds % 60, milliseconds / 10)
        //var finalTime = ((seconds*1000)+milliseconds).toString()

        /*
        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("lobby_id", lobbyId)
        jsonObject.put("player_id", intent.getStringExtra("username"))
        //jsonObject.put("level", livello)
        jsonObject.put("time", finalTime)
        RequestsHTTP.httpPOSTGameUpdate(jsonObject)

        stopTimer()
        // Setup UI for the ending of the level.
        */
        binding.layoutTutorial.visibility = View.GONE
        binding.layoutEnd.visibility = View.VISIBLE
        binding.layoutGame.visibility = View.GONE
        //binding.buttonEnd.visibility = View.INVISIBLE
        binding.timerTextView.visibility = View.INVISIBLE
    }
}