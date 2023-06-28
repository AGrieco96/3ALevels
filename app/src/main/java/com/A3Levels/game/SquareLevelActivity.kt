package com.A3Levels.game

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.A3Levels.databinding.AccelerometerActivityBinding
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random


class SquareLevelActivity : AppCompatActivity() , SensorEventListener {

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



    //up/down tra 9 e -9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccelerometerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        square = binding.mySquare
        finalPositionText = binding.finalPositionText
        goal = "Find the up/down $finalUpDown left/right $finalLeftRight position!"
        finalPositionText.text = goal

        setUpSensorStuff()
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

            if(upDown.toInt() == finalUpDown && leftRight.toInt() == finalLeftRight) {
                color = Color.GREEN
                flag = false
                startTimer()

            } else {
                color = Color.RED
            }

            // Changes the colour of the square when reaches the final position
            square.setBackgroundColor(color)
            square.text = "up/down ${upDown.toInt()}\nleft/right ${leftRight.toInt()}"
        }
    }


    private fun startTimer() {
        var timer : Timer = Timer()
        var secondsPassed : Int = 0

        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (!(upDown.toInt() == finalUpDown && leftRight.toInt() == finalLeftRight)) {
                    timer.cancel()
                    flag = true
                    return
                }
                if(secondsPassed < 3) {
                    secondsPassed++
                } else {
                    if (flag){
                        println("HAI VINTO")
                        flag = false
                        timer.cancel()
                        endGame()
                    }
                    timer.cancel()
                }
            }
        }, 0, 1000) // 1000 milliseconds = 1 second
    }

    fun endGame(){
        // End of GameLogic , so come back to the GameLevelActivity, for the sake of the execution flow
        val intent = Intent(this, GameLevelActivity::class.java)

        gameLevelExtraInfo.setlLevel(5)
        gameLevelExtraInfo.setFlag(false)
        gameLevelExtraInfo.setLobbyId(gameLevelExtraInfo.myLobbyID)
        startActivity(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }


}