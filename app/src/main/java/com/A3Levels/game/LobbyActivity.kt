package com.A3Levels.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.A3Levels.R
import com.A3Levels.auth.GoogleSignInActivity.Companion.TAG
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField

class LobbyActivity : AppCompatActivity() {

    private lateinit var lobbiesRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        FirebaseApp.initializeApp(this)

        //Retrieve user information from Cloud Firestore
        val db = FirebaseFirestore.getInstance()
        lobbiesRef = db.collection("lobbies")
        searchGame()
    }

    private fun searchGame(){
        lobbiesRef.get()
            .addOnSuccessListener { querySnapshot ->
                if(querySnapshot.isEmpty){
                    //call an handle function to upgrade lobby_id
                    getNextLobbyId()
                }
                else{
                    val lobby = querySnapshot.documents[0].id
                    Log.e(TAG,"Lobby Trovata, Join")
                    joinLobby(lobby)
                }
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error getting lobbies: $exception")
            }
        /*
        lobbiesRef.whereEqualTo("player_2","")
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty){
                    // No Lobbies found, create a new one
                    createNewLobby()
                }else {
                    val lobbyId = querySnapshot.documents[0].id
                    joinLobby(lobbyId)
                }
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error getting lobbies: $exception")
            }

         */
    }

    private fun getNextLobbyId() {
        //Query the db to get the highest lobby_id value since is incremental.
        var maxLobbyId = 0
        lobbiesRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot ){
                    val lobbyIdS = document.get("lobby_id").toString()
                    val lobbyId = lobbyIdS.toInt()
                    if (lobbyId > maxLobbyId){
                        maxLobbyId = lobbyId + 1
                        createNewLobby(maxLobbyId)
                    }
                }
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
            "player_2" to ""
        )
        lobbiesRef.add(lobby)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG,"Lobby Creata in attesa di un opponente")
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error creating lobby : $exception")
            }
    }

    private fun joinLobby(lobbyId: String){
        val lobbyRef = lobbiesRef.document(lobbyId)
        lobbyRef.get()
            .addOnSuccessListener { documentSnapshot->
                val player2 = documentSnapshot.getString("player_2")
                if(player2 != "") {
                    //Lobby full create your own.
                    getNextLobbyId()
                }else{
                    lobbyRef.update("player_2", intent.getStringExtra("username"))
                        .addOnSuccessListener {
                            // Successfully joined the lobby
                        }
                        .addOnFailureListener{ exception ->
                            Log.e(TAG,"Error creating lobby : $exception")
                        }
                }
            }

    }


}