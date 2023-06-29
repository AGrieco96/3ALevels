package com.A3Levels.game

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.A3Levels.databinding.AccelerometerActivityBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random


class SquareLevelActivity : AppCompatActivity() , SensorEventListener, gameLevelExtraInfo.TimerUpdateListener {

    private lateinit var binding : AccelerometerActivityBinding

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    private lateinit var finalPositionText: TextView
    private lateinit var goal : String

    private var color = Color.RED
    private var finalUpDown = Random.nextInt(from = -7, until = 7)
    private var finalLeftRight = Random.nextInt(from = -7, until = 7)
    private var flag : Boolean = true
    private var leftRight : Float = 0F
    private var upDown : Float = 0F

    // Singleton
    private val gameExtraInfo: gameLevelExtraInfo = gameLevelExtraInfo()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccelerometerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        square = binding.mySquare
        finalPositionText = binding.finalPositionText
        goal = "Find the up/down : $finalUpDown \n left/right : $finalLeftRight position!"
        finalPositionText.text = goal

        setUpSensorStuff()
        messageListener()
        updateCounterUI()

        // Timer Handle
        gameExtraInfo.setTimerUpdateListener(this)
        gameExtraInfo.startTimer()
    }
    override fun onTimerUpdate(minutes: Int, seconds: Int, milliseconds: Int) {
        binding.timerTextView8.text = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
    }
    override fun onTimerFinished(seconds: Int, milliseconds: Int) {
        val finalTime = ((seconds * 1000) + milliseconds).toString()
        println("finalTime $finalTime")
        gameLevelExtraInfo.setmyTime(finalTime)
    }
    fun updateCounterUI(){
        coroutineScope.launch {
            val counterValues =  gameExtraInfo.retrieveCounter(gameLevelExtraInfo.myLobbyID, gameLevelExtraInfo.myUsername)
            val player1Counter = counterValues.player1Counter
            val player2Counter = counterValues.player2Counter
            binding.textCounterP1.text = player1Counter.toString()
            binding.textCounterP2.text = player2Counter.toString()
        }
    }
    private fun setUpSensorStuff() {
        // Create the sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Checks for the sensor we have registered
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")

            // Sides = Tilting phone left(10) and right(-10)
            leftRight = event.values[0]

            // Up/Down = Tilting phone up(10), flat (0), upside-down(-10)
            upDown = event.values[1]

            square.apply {
                rotationX = upDown * 3f
                rotationY = leftRight * 3f
                rotation = -leftRight
                translationX = leftRight * -10
                translationY = upDown * 10
            }

            if((upDown.toInt() == finalUpDown) && (leftRight.toInt() == finalLeftRight) && flag) {
                flag = false
                startTimer()
            }

            if(!((upDown.toInt() == finalUpDown) && (leftRight.toInt() == finalLeftRight))) {
                color = Color.RED
            }else{
                color = Color.GREEN
            }



            // Changes the colour of the square when reaches the final position
            square.setBackgroundColor(color)
            square.text = "up/down ${upDown.toInt()} \n left/right ${leftRight.toInt()}"
        }
    }

    private fun startTimer() {
        var timer : Timer = Timer()
        var secondsPassed : Int = 0

        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                println("startTimer")
                if (!((upDown.toInt() == finalUpDown) && (leftRight.toInt() == finalLeftRight))) {
                    secondsPassed = 0
                }
                if(secondsPassed < 3) {
                    secondsPassed++
                } else {
                    println("HAI VINTO")
                    timer.cancel()
                    endGame()
                }
            }
        }, 0, 1000) // 1000 milliseconds = 1 second
    }

    fun endGame(){
        // End of GameLogic
        gameLevelExtraInfo.setlLevel(5)
        gameLevelExtraInfo.setFlag(false)
        gameLevelExtraInfo.setLobbyId(gameLevelExtraInfo.myLobbyID)
        listener?.remove()
        listener = null

        //stop timer.
        gameExtraInfo.stopTimer()

        val intent = Intent(this, GameLevelActivity::class.java)
        startActivity(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    private var listener: ListenerRegistration? = null
    fun messageListener(){
        val db = FirebaseFirestore.getInstance()
        val username = gameLevelExtraInfo.myUsername
        println("Retrieve dell'username dall'activity photo level : "+ username)
        val docMessagesRef = db.collection("messages").document(username)
        listener = docMessagesRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@EventListener
            }
            println("Snapshot attuale" + snapshot)

            if (snapshot != null && snapshot.exists()) {

                val myfield = snapshot.getString("lastMessage")
                if ( !(myfield.equals("Chat created!")) ){
                    Toast.makeText(
                        baseContext, myfield,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Log.d(ContentValues.TAG, "Current data: null")
                //Log.d(TAG, "Document ID: " + docLobbyRef.id)
            }
        })
    }
}