package com.A3Levels.game

import android.content.ContentValues
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.A3Levels.databinding.ActivityStrongboxLevelBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Timer
import java.util.TimerTask

class StrongboxLevelActivity : AppCompatActivity(), SensorEventListener, gameLevelExtraInfo.TimerUpdateListener {

    private lateinit var binding : ActivityStrongboxLevelBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var arrow: ImageView
    private val finalGrade = 60F//210F
    private var flag : Boolean = true
    private var grade = 0F

    // Singleton
    private val gameExtraInfo: gameLevelExtraInfo = gameLevelExtraInfo()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStrongboxLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        arrow = binding.arrow

        startCompass()
        messageListener()

        // Timer Handle
        gameExtraInfo.setTimerUpdateListener(this)
        gameExtraInfo.startTimer()

        updateCounterUI()

    }

    override fun onTimerUpdate(minutes: Int, seconds: Int, milliseconds: Int) {
        binding.timerTextView4.text = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
    }
    override fun onTimerFinished(seconds: Int, milliseconds: Int) {
        val finalTime = ((seconds * 1000) + milliseconds).toString()
        println("finalTime $finalTime")
        gameLevelExtraInfo.setmyTime(finalTime)
    }
    fun updateCounterUI(){
        binding.textCounterP1.text = gameLevelExtraInfo.Counter_P1.toString()
        binding.textCounterP2.text = gameLevelExtraInfo.Counter_P2.toString()
    }
    private fun startCompass(){

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also { gyroscope ->
            sensorManager.registerListener(
                this,
                gyroscope,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        // Checks for the sensor we have registered
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            val zRotation = event.values[2]

            arrow.rotation -= zRotation / 5F

            if (arrow.rotation < 0){
                grade = 360F + arrow.rotation
            } else {
                grade = arrow.rotation
            }

            if(((finalGrade - 10F <= grade) && (grade <= finalGrade + 10F)) && flag){
                println("CIAOOOOOOOOOOOOO")
                flag = false
                startTimer()
            }
        }
    }

    //Funzione da avviare nel momento in cui la freccia entra nella threshold del valore (threshold più piccola per evitare falsi negativi)
    private fun startTimer() {
        var timer : Timer = Timer()
        var secondsPassed : Int = 0

        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                println("startTimer")
                    if (!((finalGrade - 10F <= grade) && (grade <= finalGrade + 10F))) {
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
        // End of GameLogic , so come back to the GameLevelActivity, for the sake of the execution flow
        gameLevelExtraInfo.setlLevel(3)
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