package com.A3Levels.game

import android.os.Handler
import android.os.Looper


abstract class Level_0(private val levelCallback: Level_0.TimerCallback) {

    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0
    private var deciseconds = 0
    private var milliseconds = 0
    private lateinit var timerCallback: TimerCallback
    private var isRunning = false

    fun startTimer(callback: TimerCallback) {
        if (!isRunning) {
            //Start Timer
            timerCallback = callback
            isRunning = true
            handler.postDelayed(updateTimer, 10)
        }

    }
    fun stopTimer() {
        if (isRunning) {
            isRunning = false
            handler.removeCallbacks(updateTimer)
        }
    }

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

            timerCallback.onTimerUpdate(String.format("%02d:%02d.%02d", seconds / 60, seconds % 60, milliseconds / 10))

            if (isRunning) {
                handler.postDelayed(this, 10)
            }
        }
    }

    interface TimerCallback {
        fun onTimerUpdate(time: String)
    }
}