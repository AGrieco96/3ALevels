package com.A3Levels.game

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.A3Levels.databinding.ActivityStrongboxLevelBinding
import java.util.Timer
import java.util.TimerTask

class StrongboxLevelActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding : ActivityStrongboxLevelBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var arrow: ImageView
    private val finalGrade = 210F
    private var flag : Boolean = true
    private var grade = 0F



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStrongboxLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        arrow = binding.arrow

        startCompass()
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

            if((finalGrade - 10F <= grade) && (grade <= finalGrade + 10F) && flag){
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
                    if (!((finalGrade - 10F <= grade) && (grade <= finalGrade + 10F))) {
                        timer.cancel()
                        flag = true
                        return
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
        val intent = Intent(this, GameLevelActivity::class.java)

        // Gli dovremmo passare alcuni parametri, come se deve visualizzare o meno lo start o la fine del tutorial.
        // intent.putExtra("username", username) - # del livello etc.
        intent.putExtra("level",3)
        intent.putExtra("flag",false)
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