package com.A3Levels.game

import android.os.Handler
import android.os.Looper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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

        /*
        //Counter
        var Counter_P1: Int = 0
        var Counter_P2: Int = 0
        fun setMyCounter_P1(counter:Int){
            Counter_P1 = counter
        }
        fun setMyCounter_P2(counter:Int){
            Counter_P2 = counter
        }

         */

    }

    /*
       Global Handler Timer
    */
    private val handler = Handler(Looper.getMainLooper())
    private var deciseconds = 0
    var seconds = 0
    var milliseconds = 0

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


    data class CounterValues(val player1Counter: Int, val player2Counter: Int)
    suspend fun retrieveCounter(lobbyId: String, player:String): CounterValues {
        val db = FirebaseFirestore.getInstance()

        //val lobbyId = gameLevelExtraInfo.myLobbyID
        //var player1:String = gameLevelExtraInfo.myUsername
        var player1:String = player
        var player2:String = ""
        var player_1Counter:Int = 0
        var player_2Counter:Int = 0

        val docLobbyRef = db.collection("lobbies").document(lobbyId).get().await()
        if (docLobbyRef.exists()) {
            val lobbieData = docLobbyRef.data
            var playerCloud = (lobbieData?.get("player_1") as? String).toString()
            if( player1.equals(playerCloud)) {
                player2 = (lobbieData?.get("player_2") as? String).toString()
            }else
                player2 = (lobbieData?.get("player_1") as? String).toString()

            println("Players: $player1 + $player2")
        }

        val docGamesRef = db.collection("games").document(lobbyId).get().await()
        if (docGamesRef.exists()) {
            val gameData = docGamesRef.data
            val counter1 = (gameData?.get(player1) as? Map<*, *>)?.get("counter")
            val counter2 = (gameData?.get(player2) as? Map<*, *>)?.get("counter")
            player_1Counter = counter1?.toString()?.toIntOrNull() ?: 0
            player_2Counter = counter2?.toString()?.toIntOrNull() ?: 0
        }

        /*
        val docLobbyRef = db.collection("lobbies").document(lobbyId)
        docLobbyRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()){
                val lobbieData = documentSnapshot.data
                player1 = (lobbieData?.get("player_1") as? String).toString()
                player2 = (lobbieData?.get("player_2") as? String).toString()
                println("Players : "+player1+" + "+player2)
            }
        }
        val docGamesRef = db.collection("games").document(lobbyId)
        docGamesRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists() && documentSnapshot != null ) {

                val gameData = documentSnapshot.data
                val counter_1 = (gameData?.get(player1) as? Map<*, *>)?.get("counter")
                val counter_2 = (gameData?.get(player2) as? Map<*, *>)?.get("counter")
                player_1Counter = counter_1?.toString()?.toIntOrNull() ?: 0
                player_2Counter = counter_2?.toString()?.toIntOrNull() ?: 0

            } else {
                // Il documento non esiste
            }

        }.addOnFailureListener { exception ->
            // Gestisci l'errore in caso di fallimento della lettura
        }

         */

        return CounterValues(player_1Counter, player_2Counter)
    }

}