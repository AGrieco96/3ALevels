package com.A3Levels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.A3Levels.auth.GoogleSignInActivity.Companion.TAG
import com.A3Levels.auth.LoginEmailActivity
import com.A3Levels.auth.RegisterEmailActivity
import com.A3Levels.databinding.ActivityHomeBinding
import com.A3Levels.game.LobbyActivity
import com.A3Levels.other.CreditsActivity
import com.A3Levels.other.OptionActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


//Hello
class HomeActivity : AppCompatActivity() {
    data class User(
        val displayName: String? = null
    )
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Retrieve user information from Cloud Firestore
        val db = FirebaseFirestore.getInstance()
        val userUID = FirebaseAuth.getInstance().currentUser?.uid
        var username = ""
        if( userUID != null ){
            val userRef = db.collection("users").document(userUID)
            userRef.get().addOnSuccessListener{ document ->
                if ( document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    username = user?.displayName.toString()
                    //update ui
                    binding.usernameView.text=user?.displayName
                    resources.getString(R.string.username_login, user?.displayName)
                }else{
                    Log.d(TAG,"No document")
                }
            }.addOnFailureListener{ exception ->
                Log.d(TAG,"Error getting document: ", exception)
            }
        }

        binding.buttonOption.setOnClickListener {
            display_options()
            finish()
        }

        binding.buttonCredits.setOnClickListener{
            display_credits()
        }

        binding.buttonLogout.setOnClickListener{
            logout()
        }

        binding.buttonStart.setOnClickListener{
            startGame(username)
        }




        //setContentView(R.layout.activity_home)

    }

    private fun display_options(){
        val intent = Intent(this, OptionActivity::class.java)
        startActivity(intent)
    }
    private fun display_credits(){
        val intent = Intent(this, CreditsActivity::class.java)
        startActivity(intent)
    }

    private fun logout(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginEmailActivity::class.java)
        startActivity(intent)
    }

    private fun startGame(username:String){
        val intent = Intent(this, LobbyActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }
}