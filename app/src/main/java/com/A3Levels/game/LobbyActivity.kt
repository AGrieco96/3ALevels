package com.A3Levels.game

import android.content.Intent
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.MainActivity
import com.A3Levels.R
import com.A3Levels.auth.GoogleSignInActivity.Companion.TAG
import com.A3Levels.other.APIService
import com.A3Levels.other.RequestsHTTP
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import io.grpc.internal.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit


class LobbyActivity : AppCompatActivity() {

    private lateinit var lobbiesRef: CollectionReference
    private val db = FirebaseFirestore.getInstance()
    private var lobbyId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        FirebaseApp.initializeApp(this)

        //Retrieve user information from Cloud Firestore
        // val db = FirebaseFirestore.getInstance()
        lobbiesRef = db.collection("lobbies")
        searchGame()
    }

    private fun searchGame(){
        var flag:Boolean = false
        lobbiesRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot ) {
                    val status = document.get("status").toString()
                    if (status.equals("waiting")) {
                        flag = true
                        lobbyId = document.id
                    }
                }
                if(flag){
                    // Lobby found
                    //lobbyId = querySnapshot.documents[0].id
                    joinLobby(lobbyId)
                    setupListener(lobbyId)
                } else {
                    // Lobby not found. increase id and create new one.
                    getNextLobbyId()
                    println("FLAG FALSE")
                }
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error getting lobbies: $exception")
            }
    }

    private fun getNextLobbyId() {
        //Query the db to get the highest lobby_id value since is incremental.
        var maxLobbyId = 0
        lobbiesRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot ){
                    val lobbyIdS = document.get("lobby_id").toString()
                    val lobbyId_field = lobbyIdS.toInt()
                    if (lobbyId_field >= maxLobbyId){
                        maxLobbyId = lobbyId_field + 1
                    }
                }
                createNewLobby(maxLobbyId)
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error getting lobbies : $exception")
            }
    }
    private fun createNewLobby(maxLobbyId:Int){
        //create new istance on db of lobbies.
        val lobby = hashMapOf(
            "lobby_id" to maxLobbyId,
            "player_1" to intent.getStringExtra("username"),
            "player_2" to "",
            "status" to "waiting"
        )
        lobbiesRef.add(lobby)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG,"Lobby Creata in attesa di un opponente")
                lobbyId = documentReference.id
                setupListener(lobbyId)
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error creating lobby : $exception")
            }

    }

    private fun joinLobby(lobbyId: String){
        val lobbyRef = lobbiesRef.document(lobbyId)
        lobbyRef.get()
            .addOnSuccessListener { documentSnapshot->
                lobbyRef.update("player_2", intent.getStringExtra("username"))
                    .addOnSuccessListener {
                        lobbyRef.update("status","started")
                            val lobbyId = documentSnapshot.get("lobby_id").toString()

                        // Create JSON using JSONObject
                        val jsonObject = JSONObject()
                        jsonObject.put("lobby_id", lobbyId)

                        RequestsHTTP.httpPOST(jsonObject)

                    }
                    .addOnFailureListener{ exception ->
                        Log.e(TAG,"Error On joining lobby : $exception ")
                    }
            }

    }

    private fun setupListener(lobbyId: String){
        println("LobbyID :   "+ lobbyId)
        val docLobbyRef = db.collection("lobbies").document(lobbyId)
        val listener = docLobbyRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            if (snapshot != null && snapshot.exists()) {
                val myfield = snapshot.getString("status")
                // Log.d(TAG, "Current data: ${snapshot.data}")
                Log.d(TAG, "Current data: $myfield")
                if (myfield.equals("started")) {
                    val intent = Intent(this, TestActivity::class.java)
                    startActivity(intent)
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


}