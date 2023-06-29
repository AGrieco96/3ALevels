package com.A3Levels.game

import android.content.ContentValues
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.HomeActivity
import com.A3Levels.databinding.BallActivityBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BallActivity : AppCompatActivity() , SensorEventListener, gameLevelExtraInfo.TimerUpdateListener {

    // Binding
    private lateinit var binding: BallActivityBinding

    private var flag:Boolean = true
    //Sensor
    private lateinit var sensorManager: SensorManager
    private var gravitySensor: Sensor? = null

    //The ball to move around on the screen
    private var ball: ImageView? = null

    //The width of the frame
    private var frameWidth = 0

    //The height of the frame
    private var frameHeight = 0

    //Used to make the ball stay within the frame
    private var radius = 0

    //Used to play a sound when the ball hits the edge of the frame
    //private var toneGenerator: ToneGenerator? = null

    //Gets the screens height and width
    var displayMetrics: DisplayMetrics? = null
    var devHeight = 0
    var devWidth = 0

    // Singleton
    private val gameExtraInfo: gameLevelExtraInfo = gameLevelExtraInfo()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BallActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Gets the frame and the ball
        val frame = binding.frame
        ball = binding.ball

        //Initiates a new ToneGenerator
        //toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)

        //Sets the height and with of the frame
        frameWidth = frame.width
        frameHeight = frame.height

        //Sets the radius
        radius = 25

        //Gets the system sensors
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        assert(sensorManager != null)
        //Selects the gravitySensor sensor
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        //Gets the screens height and width
        val displayMetrics = DisplayMetrics()

        // on below line we are getting metrics
        // for display using window manager.
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        // on below line we are getting height
        // and width using display metrics.
         devHeight = displayMetrics.heightPixels
         devWidth = displayMetrics.widthPixels

        //Creates a listener on the sensor
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
        //Set the delay to GAME as that created a less "laggy" experience

        messageListener()
        updateCounterUI()

        // Timer Handle
        gameExtraInfo.setTimerUpdateListener(this)
        gameExtraInfo.startTimer()

    }
    override fun onTimerUpdate(minutes: Int, seconds: Int, milliseconds: Int) {
        binding.timerTextView6.text = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
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


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        //Current coordinates of the ball
        val X = ball!!.x
        val Y = ball!!.y

        //The coordinates the ball moves to
        val nextX = X - sensorEvent.values[0]
        val nextY = Y + sensorEvent.values[1]

        //moves the ball LEFT until it crashes with the border
        if (nextX - radius >= frameWidth / 2) {
            ball!!.x = nextX
        } else {
            //onFrameHit()
            //"Bounces" the ball of the frame
            //ball!!.x = nextX + 35
        }
        //moves the ball to the TOP until it crashes with the border
        //Added "+ 20" to make the ball bounce on the inner edge of the frame
        if (nextY - radius >= frameHeight / 2 + 20) {
            ball!!.y = nextY
        } else {
            //onFrameHit()
            //ball!!.y = nextY + 35
        }

        //moves the ball to the RIGHT until it crashes with the border
        //Added "- 60" to make the ball bounce on the frame and not the edge of the screen
        //if (nextX + radius >=  1024 ) {
        if ((nextX + radius) > devWidth - (frameWidth / 2) - 60) {
            //onFrameHit()
            //ball!!.x = 1024.0F -150
            ball!!.x =(nextX - 35)

        }

        //moves the ball to the BOTTOM until it crashes with the border
        //Added "- 320" to make the ball bounce on the frame and off the edge of the screen
        //if (nextY + radius >= 2048  ) {
        if ((nextY + radius) > devHeight - (frameHeight / 2) - 320) {

            //onFrameHit()
            //ball!!.y = 2048.0F - 150
            ball!!.y = (nextY - 35)
            //println("ASSE X" + nextX)
        }
        //println("BAllx : " + ball!!.x)
        //println("BallY" + ball!!.y)


        if ( (ball!!.x >= 400 && ball!!.x <= 540) && (ball!!.y >= 955 && ball!!.y <= 1095) && flag){
            println(" HAI VINTOOOOOOOOOOOOOOOOOOOO")
            flag = false
            //Removes the listener when the app is not in use
            sensorManager.unregisterListener(this)
            endGame()
        }
    }

    fun endGame(){
        // End of GameLogic
        gameLevelExtraInfo.setlLevel(6)
        gameLevelExtraInfo.setFlag(false)
        gameLevelExtraInfo.setLobbyId(gameLevelExtraInfo.myLobbyID)
        listener?.remove()
        listener = null

        //stop timer.
        gameExtraInfo.stopTimer()

        val intent = Intent ( this, GameLevelActivity::class.java)
        startActivity(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, i: Int) {
        //NOT USED, but implemented as SensorEventListener needs it
    }

    override fun onResume() {
        super.onResume()
        //Creates a listener on the gravity-sensor on resuming the app
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        //Removes the listener when the app is not in use
        sensorManager.unregisterListener(this)
    }

    /**
     * Sets of multiple effects when the ball hits the edge of the frame
     */
  /*
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun onFrameHit() {
        //Vibrator
        val vibrator = (getSystemService(VIBRATOR_SERVICE) as Vibrator)
        val vibrationEffect2: VibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)

        // it is safe to cancel other vibrations currently taking place
        vibrator.cancel()
        vibrator.vibrate(vibrationEffect2)

        //Sound
        toneGenerator!!.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP)
    }

*/

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

