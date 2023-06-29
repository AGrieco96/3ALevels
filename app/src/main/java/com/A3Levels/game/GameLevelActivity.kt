package com.A3Levels.game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.A3Levels.graphics.PyramidActivity
import com.A3Levels.R
import com.A3Levels.auth.GoogleSignInActivity
import com.A3Levels.databinding.ActivityTestLevelBinding
import com.A3Levels.other.RequestsHTTP
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import kotlin.random.Random


class GameLevelActivity : AppCompatActivity(){

    private lateinit var binding: ActivityTestLevelBinding
    var lobbyId : String = "0"
    //private lateinit var username : String
    //private var flagMatch : Boolean = true;
    private var counterLevel : Int = 0;
    private var listener: ListenerRegistration? = null
    lateinit var username : String
    var flagMessage:Boolean = true

    // Variable for counter UI
    private val gameExtraInfo: gameLevelExtraInfo = gameLevelExtraInfo()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Init
        binding = ActivityTestLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)


        //Retrieve the level in order to launch the correct activity.
        lobbyId = gameLevelExtraInfo.myLobbyID
        println("lobbyId : "+lobbyId)
        counterLevel = gameLevelExtraInfo.myLevel
        println("Check sul flag su onCreate : " +gameLevelExtraInfo.myFlag)
        println("Check sul livello : " +gameLevelExtraInfo.myLevel)
        //username = gameLevelExtraInfo.myUsername
        //println("Check sull'username ad ogni avvio di GameLevelActivity  : " +username)
        set_game_UI(gameLevelExtraInfo.myFlag)

        /*
        *   if(flag){
        *     Visualizzazione start match.
        *   }
        *   else{
        *       Visualizzazione end match + attivazione listener
        *   }
        * */
        binding.btnMsg1.setOnClickListener{
            messagePost(1)
        }
        binding.btnMsg2.setOnClickListener{
            messagePost(2)
        }
        binding.btnMsg3.setOnClickListener{
            messagePost(3)
        }
        binding.btnMsg4.setOnClickListener{
            messagePost(4)
        }
        binding.btnMsg5.setOnClickListener{
            messagePost(5)
        }

        binding.displayMessage.setOnClickListener{
            displayMessages()
        }
    }

    // UI Function
    fun set_game_UI(flagMatch: Boolean){
        if(!(flagMatch)){
            set_endgame_UI()
        }else{
            setPersonalInfoLevelUI()
            val animator = createAnimation()
            animator.start()
        }
    }
    private fun createAnimation(): Animator {
        println("Eseguo il createAnimation?")

        val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
        animator.duration = 6000
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            //Apply effect
            binding.layoutTutorial.alpha = value
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                println("On Animation Start")
                binding.layoutTutorial.visibility = View.VISIBLE
                binding.layoutEnd.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animator) {
                println("On Animation End")
                binding.layoutTutorial.visibility = View.GONE
                startLevel()
            }
        })

        return animator
    }
    fun setPersonalInfoLevelUI(){

        binding.layoutTutorial.findViewById<TextView>(R.id.levelText).text = counterLevel.toString()
        when(counterLevel){
            1 -> {
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_1)
            }
            2 -> {
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_2)
            }
            3-> {
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_3)
            }
            4->{
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_4)
            }
            5->{
                val textView_Tut = binding.layoutTutorial.findViewById<TextView>(R.id.TutorialText)
                textView_Tut.text = getString(R.string.tutorial_level_5)
            }
        }
    }
    private fun set_endgame_UI(){
        // UI changes
        binding.layoutTutorial.visibility = View.GONE
        binding.layoutEnd.visibility = View.VISIBLE
        binding.timerEndView.text = gameLevelExtraInfo.myTime
        // Handler
        advancedPost()
        /*
            coroutineScope.launch {
                val counterValues = retrieveCounter()
                var player1Counter:Int = counterValues.player1Counter.toInt()
                var player2Counter:Int = counterValues.player2Counter.toInt()
                gameLevelExtraInfo.setMyCounter_P1(player1Counter)
                gameLevelExtraInfo.setMyCounter_P2(player2Counter)
            }
        */
        if ( counterLevel != 6 )
            setupListener()
        else
            startLevel()

    }
    private fun displayMessages(){
        if(flagMessage) {
            binding.boxMsg.visibility = View.VISIBLE
            flagMessage = false
        }
        else {
            binding.boxMsg.visibility = View.GONE
            flagMessage = true
        }

    }
    // Function over the network
    private fun advancedPost(){
        /* Retrieve dell'username */
        //val time = Random.nextInt(from = 10, until = 60).toString()
        username = gameLevelExtraInfo.myUsername
        val time = gameLevelExtraInfo.myTime

        if(counterLevel-1 == 1){
            // Post sempre uguale
            val objectphoto = gameLevelExtraInfo.myObjectInPhoto
            val images = gameLevelExtraInfo.myImage

            // Create JSON using JSONObject
            val jsonObject = JSONObject()
            jsonObject.put("object", objectphoto)
            jsonObject.put("image", images)
            jsonObject.put("lobby_id", lobbyId)
            jsonObject.put("player_id", username)
            jsonObject.put("time",time)
            RequestsHTTP.httpPOSTphotoAI(jsonObject)

        }else {
            //HTTP Request for others level
            val jsonObject = JSONObject()
            jsonObject.put("lobby_id", lobbyId)
            jsonObject.put("player_id", username)
            jsonObject.put("time",time)
            RequestsHTTP.httpPOSTGameUpdate(jsonObject)
        }

    }
    private fun messagePost(idMessage : Int){
        username = gameLevelExtraInfo.myUsername
        lateinit var message : String
        when(idMessage){
            1 -> {
                message = binding.btnMsg1.text.toString()
            }
            2 -> {
                message = binding.btnMsg2.text.toString()
            }
            3 -> {
                message = binding.btnMsg3.text.toString()
            }
            4 -> {
                message = binding.btnMsg4.text.toString()
            }
            5 -> {
                message = binding.btnMsg5.text.toString()
            }
        }

        val jsonObject = JSONObject()
        jsonObject.put("player_id",username)
        jsonObject.put("lobby_id",lobbyId)
        jsonObject.put("message",message)
        RequestsHTTP.httpPUTsendMessage(jsonObject)
    }

    // Execution Flow Function
    fun startLevel(){
        listener?.remove()
        listener = null
        println("Livello che sta per essere startato : "+counterLevel)
        when(counterLevel){
            1 -> {
                val intent = Intent(this, LevelPhotoActivity::class.java)
                gameLevelExtraInfo.setUsername(gameLevelExtraInfo.myUsername)
                startActivity(intent)
            }
            2 -> {
                val intent = Intent(this, StrongboxLevelActivity::class.java)
                gameLevelExtraInfo.setUsername(gameLevelExtraInfo.myUsername)
                gameLevelExtraInfo.setLobbyId(gameLevelExtraInfo.myLobbyID)
                startActivity(intent)
            }

            3 -> {
                val intent = Intent(this, LevelJumpActivity::class.java)
                gameLevelExtraInfo.setUsername(gameLevelExtraInfo.myUsername)
                startActivity(intent)
            }
            4 -> {
                val intent = Intent(this, SquareLevelActivity::class.java)
                gameLevelExtraInfo.setUsername(gameLevelExtraInfo.myUsername)
                startActivity(intent)
            }
            5 -> {
                val intent = Intent(this, BallActivity::class.java)
                gameLevelExtraInfo.setUsername(gameLevelExtraInfo.myUsername)
                startActivity(intent)
            }
            6 -> {
                checkWinner()
                //* Display the end game
                val intent = Intent ( this, PyramidActivity::class.java)
                startActivity(intent)
            }
        }

    }
    private fun setupListener(){

        val db = FirebaseFirestore.getInstance()

        println("LobbyID :   "+ lobbyId)
        val docGamesRef = db.collection("games").document(lobbyId)
        listener = docGamesRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }
            println("Snapshot attuale" + snapshot)

            if (snapshot != null && snapshot.exists()) {

                val myfield = snapshot.getString("level")
                // Log.d(TAG, "Current data: ${snapshot.data}")
                Log.d(TAG, "Current data: $myfield")

                var newLevel = counterLevel
                var dbLevel = myfield?.toInt()
                println("newLevel : "+newLevel+" dbLevel : "+dbLevel)
                println("Risultato compare : " +(newLevel==dbLevel))

                if (newLevel==dbLevel) {
                    set_game_UI(true)
                    //startLevel()
                }
                /*
                val player2 = snapshot.getString("player_1").toString()
                val flagPlayer:Boolean = player2.isEmpty()
                println("Player2   : " + player2)
                println("FlagPlayer :  " + player2.isEmpty())
                if(!flagPlayer){
                    // Log.d(TAG, "Document ID: " + docLobbyRef.id)
                    // Handle the changes to the field in the document

                }
                */
            } else {
                Log.d(TAG, "Current data: null")
                //Log.d(TAG, "Document ID: " + docLobbyRef.id)
            }
        })



    }

    /*
    private fun checkWinner(){
        val db = FirebaseFirestore.getInstance()

        val lobbyId = gameLevelExtraInfo.myLobbyID
        var player1:String = gameLevelExtraInfo.myUsername
        var player2:String = ""
        var player_1Counter:Int = 0
        var player_2Counter:Int = 0

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

                if(player_1Counter > player_2Counter){
                    //player_1 is always the player who is acting on the telephone
                    //binding.result.text = "WINNER"
                    gameLevelExtraInfo.setFlagVictory(true)
                }else {
                    //binding.result.text = "LOSER"
                    gameLevelExtraInfo.setFlagVictory(false)
                }

            } else {
                // Il documento non esiste
            }

        }.addOnFailureListener { exception ->
            // Gestisci l'errore in caso di fallimento della lettura
        }

    }
    */

    /*
    private suspend fun retrieveCounter(): CounterValues {
        val db = FirebaseFirestore.getInstance()

        val lobbyId = gameLevelExtraInfo.myLobbyID
        var player1:String = gameLevelExtraInfo.myUsername
        var player2:String = ""
        var player_1Counter:Int = 0
        var player_2Counter:Int = 0

        val docLobbyRef = db.collection("lobbies").document(lobbyId).get().await()
        if (docLobbyRef.exists()) {
            val lobbieData = docLobbyRef.data
            player1 = (lobbieData?.get("player_1") as? String).toString()
            player2 = (lobbieData?.get("player_2") as? String).toString()
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
    */
    private fun checkWinner(){

        coroutineScope.launch {
            val counterValues = gameExtraInfo.retrieveCounter()
            val player1Counter = counterValues.player1Counter
            val player2Counter = counterValues.player2Counter

            if (player1Counter > player2Counter) {
                //player_1 is always the player who is acting on the telephone
                //binding.result.text = "WINNER"
                gameLevelExtraInfo.setFlagVictory(true)
            } else {
                //binding.result.text = "LOSER"
                gameLevelExtraInfo.setFlagVictory(false)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancella la coroutine quando l'attività viene distrutta
    }

}