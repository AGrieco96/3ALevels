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



class LaMiaPrimaVolta : AppCompatActivity() , SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gyroscope_activity)

        // Keeps phone in light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        square = findViewById(R.id.my_square)

        setUpSensorStuff()
    }

    private fun setUpSensorStuff() {
        // Create the sensor manager
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
            //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")


            val xRotation = event.values[0]
            val yRotation = event.values[1]
            val zRotation = event.values[2]

            square.apply {
                rotationX = xRotation * 3f
                rotationY = yRotation * 3f
            }

            // Changes the colour of the square if it's completely flat
            //val color = if (upDown.toInt() == 0 && sides.toInt() == 0) Color.GREEN else Color.RED
            //square.setBackgroundColor(color)

            square.text = "X Axis ${xRotation.toInt()}\n Y Axis ${yRotation.toInt()}"
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