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
                    createNewLobby()
                }
                else{
                    val lobby = querySnapshot.documents[0].id
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
    private fun createNewLobby(){
        //create new istance on db of lobbies.
        val lobby = hashMapOf(
            "lobby_id" to getNextLobbyId(),
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
        lobbyRef.update("player_2", intent.getStringExtra("username"))
            .addOnSuccessListener {
                // Successfully joined the lobby
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error creating lobby : $exception")
            }
    }

    private fun getNextLobbyId():Int {
        //Query the db to get the highest lobby_id value since is incremental.
        var maxLobbyId = 0
        lobbiesRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot ){
                    val lobbyId = document.getLong("lobby_id")?.toInt() ?: 0
                    if (lobbyId > maxLobbyId){
                        maxLobbyId = lobbyId
                    }
                }
            }
            .addOnFailureListener{ exception ->
                Log.e(TAG,"Error getting lobbies : $exception")
            }
        return maxLobbyId + 1
    }
}