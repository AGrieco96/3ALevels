package com.A3Levels

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
import kotlin.random.Random.Default.nextInt


class LaMiaPrimaVolta : AppCompatActivity() , SensorEventListener {

    private lateinit var binding : AccelerometerActivityBinding

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    private lateinit var finalPositionText: TextView
    private val finalUpDown : Int = nextInt(-9, 9)
    private val finalLeftRight : Int = nextInt(-9, 9)


    //up/down tra 9 e -9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccelerometerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        square = binding.mySquare
        finalPositionText = binding.finalPositionText

        finalPositionText.setText(finalLeftRight)

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
            val leftRight = event.values[0]

            // Up/Down = Tilting phone up(10), flat (0), upside-down(-10)
            val upDown = event.values[1]

            square.apply {
                rotationX = upDown * 3f
                rotationY = leftRight * 3f
                rotation = -leftRight
                translationX = leftRight * -10
                translationY = upDown * 10
            }

            // Changes the colour of the square when reaches the final position
            val color = if (upDown.toInt() == finalUpDown && leftRight.toInt() == finalLeftRight) Color.GREEN else Color.RED
            square.setBackgroundColor(color)
            square.text = "up/down ${upDown.toInt()}\nleft/right ${leftRight.toInt()}"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }


}