package com.A3Levels.game

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.A3Levels.R
import com.A3Levels.auth.GoogleSignInActivity
import com.A3Levels.databinding.ActivityTestLevelBinding
import com.A3Levels.other.RequestsHTTP
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import kotlin.random.Random


class GameLevelActivity : AppCompatActivity(){
    private lateinit var binding: ActivityTestLevelBinding
    private lateinit var lobbyId : String
    private lateinit var username : String
    private var flagMatch : Boolean = true;
    private var counterLevel : Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Init
        binding = ActivityTestLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        set_game_UI(flagMatch)

        /*
        *   if(flag){
        *     Visualizzazione start match.
        *   }
        *   else{
        *       Visualizzazione end match + attivazione listener
        *   }
        * */

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

    // setupInit Function
    fun init(){
        lobbyId = intent.getStringExtra("lobbyId").toString()
        username = intent.getStringExtra("username").toString()
        counterLevel = intent.getIntExtra("level",0)
        flagMatch = intent.getBooleanExtra("flag",true )
        println("Counter level : " + counterLevel)
        println("Flag : "+ flagMatch)

    }
    // UI Function
    fun set_game_UI(flagMatch: Boolean){
        if(!(flagMatch)){
            set_endgame_UI()
        }else{
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
                    //binding.layoutGame.visibility = View.INVISIBLE
                    //binding.buttonEnd.visibility = View.GONE
                    //binding.timerTextView.visibility = View.INVISIBLE
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
    }

    fun setPersonalInfoLevelUI(){

        binding.layoutTutorial.findViewById<TextView>(R.id.levelText).text = counterLevel.toString()
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
            4->{
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_4)
            }
            5->{
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_5)
            }
        }
    }
    private fun set_endgame_UI(){
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
        //binding.layoutGame.visibility = View.GONE
        //binding.buttonEnd.visibility = View.INVISIBLE
        //binding.timerTextView.visibility = View.INVISIBLE

        // Handler
        advancedPost()
        setupListener()
        //set_game_UI(true)

    }

    // Execution Flow Function
    fun startLevel(){
        when(counterLevel){
            1 -> {
                val intent = Intent(this, LevelPhotoActivity::class.java)
                intent.putExtra("lobbyId", lobbyId)
                intent.putExtra("username", username)
                startActivity(intent)
                }
            2 -> {
                val intent = Intent(this, StrongboxLevelActivity::class.java)
                //intent.putExtra("lobbyId", lobbyId)
                //intent.putExtra("username", username)
                startActivity(intent)
            }

            3 -> {
                val intent = Intent(this, LevelJumpActivity::class.java)
                //intent.putExtra("lobbyId", lobbyId)
                //intent.putExtra("username", username)
                startActivity(intent)
            }
            4 -> {
                val intent = Intent(this, SquareLevelActivity::class.java)
                //intent.putExtra("lobbyId", lobbyId)
                //intent.putExtra("username", username)
                startActivity(intent)
            }
            5 -> {
                val intent = Intent(this, BallActivity::class.java)
                //intent.putExtra("lobbyId", lobbyId)
                //intent.putExtra("username", username)
                startActivity(intent)
            }
        }

    }

    private fun advancedPost(){
        var time = Random.nextInt(from = 10, until = 60).toString()
        if(counterLevel-1 != 1){
            // Post sempre uguale
            var objectphoto = intent.getStringExtra("object").toString()
            var images = intent.getStringExtra("image").toString()

            // Create JSON using JSONObject
            val jsonObject = JSONObject()
            jsonObject.put("object", objectphoto)
            jsonObject.put("image", images)
            jsonObject.put("lobby_id", lobbyId)
            jsonObject.put("player_id", username)
            jsonObject.put("time",time)
            RequestsHTTP.httpPOSTphotoAI(jsonObject)

        }else {
            //Post per il photo level
            val jsonObject = JSONObject()
            jsonObject.put("lobby_id", lobbyId)
            jsonObject.put("player_id", username)
            jsonObject.put("time",time)
            RequestsHTTP.httpPOSTGameUpdate(jsonObject)
        }

    }

    private val db = FirebaseFirestore.getInstance()
    private fun setupListener(){
        FirebaseApp.initializeApp(this)

        println("LobbyID :   "+ lobbyId)
        val docGamesRef = db.collection("games").document(lobbyId)
        val listener = docGamesRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            if (snapshot != null && snapshot.exists()) {
                val myfield = snapshot.getString("level")
                // Log.d(TAG, "Current data: ${snapshot.data}")
                Log.d(TAG, "Current data: $myfield")
                var newLevel = counterLevel.toString()
                if (myfield.equals(newLevel)) {
                    set_game_UI(true)
                }
                /*
                val player2 = snapshot.getString("player_1").toString()
                val flagPlayer:Boolean = player2.isEmpty()
                println("Player2   : " + player2)
                println("FlagPlayer :  " + player2.isEmpty())
                if(!flagPlayer){
                    // Log.d(TAG, "Document ID: " + docLobbyRef.id)
                    // Handle the changes to the field in the document

                }
                */
            } else {
                Log.d(GoogleSignInActivity.TAG, "Current data: null")
                //Log.d(TAG, "Document ID: " + docLobbyRef.id)
            }
        })



    }


}