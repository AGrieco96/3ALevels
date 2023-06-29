package com.A3Levels.game

import android.os.Handler
import android.os.Looper

class gameLevelExtraInfo {

    companion object  {
        // Global Information to handle
        lateinit var myUsername:String
        var myLobbyID:String = "1"
        var myLevel:Int = 0
        var myFlag:Boolean = true


        fun setUsername(username:String){
            myUsername = username.toString()
        }
        fun setLobbyId(lobbyID:String){
            myLobbyID = lobbyID.toString()
        }
        fun setlLevel(level: Int){
            myLevel = level
        }
        fun setFlag(flag:Boolean){
            myFlag = flag
        }

        //Handle the specific case for photoLevel
        lateinit var myObjectInPhoto: String
        lateinit var myImage : String
        fun setObjectInPhoto(objectInPhoto : String){
            myObjectInPhoto = objectInPhoto
        }
        fun setImage ( imageString : String ){
            myImage = imageString
        }

        private var instance: gameLevelExtraInfo? = null

        // Flag to declare victory
        var myflagVictory:Boolean = true
        fun setFlagVictory(flag:Boolean){
            myflagVictory = flag
        }

        // Time
        var myTime:String = ""
        fun setmyTime(time:String){
            myTime = time
        }

        //Counter
        var Counter_P1: Int = 0
        var Counter_P2: Int = 0
        fun setMyCounter_P1(counter:Int){
            Counter_P1 = counter
        }
        fun setMyCounter_P2(counter:Int){
            Counter_P2 = counter
        }


    }

    /*
       Global Handler Timer
    */
    private val handler = Handler(Looper.getMainLooper())
    private var deciseconds = 0
    var seconds = 0
    var milliseconds = 0

    fun myGetSeconds(): Int {
        return seconds
    }
    fun myGetMilliseconds(): Int {
        return milliseconds
    }


    // Timer logic handler
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
            //binding.timerTextView.text = String.format("%02d:%02d.%02d", seconds / 60, seconds % 60, milliseconds / 10)
            timerUpdateListener?.onTimerUpdate(seconds / 60, seconds % 60, milliseconds / 10)

            handler.postDelayed(this, 10)
        }
    }
    fun startTimer() {
        handler.post(updateTimer)
    }
    fun stopTimer() {
        println("Entro dentro stop timer?")
        handler.removeCallbacks(updateTimer)
        val timerSeconds = seconds
        val timerMilliseconds = milliseconds
        seconds = 0
        deciseconds = 0
        milliseconds = 0
        timerUpdateListener?.onTimerFinished(timerSeconds, timerMilliseconds)
    }
    // To adding interfaces.
    private var timerUpdateListener: TimerUpdateListener? = null
    fun setTimerUpdateListener(listener: TimerUpdateListener) {
        timerUpdateListener = listener
    }
    interface TimerUpdateListener {
        fun onTimerUpdate(minutes: Int, seconds: Int, milliseconds: Int)
        fun onTimerFinished(seconds: Int, milliseconds: Int)
    }

}