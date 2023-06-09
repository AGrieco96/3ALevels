package com.A3Levels.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.databinding.RegisterEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RegisterEmailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: RegisterEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterEmailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        binding.buttonRegister.setOnClickListener {
            register()
        }

        binding.textViewLogin.setOnClickListener {
            goToLogin()
        }
    }

    private fun register(){
        val email = binding.editTextEmailAddress.text.toString()
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        val username = binding.editTextUserName.text.toString()

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || username.isBlank()) {
            Toast.makeText(this, "No field can be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val userFirebase = auth.currentUser
                if(userFirebase != null) {
                    addUserToDB(userFirebase, username)
                }
            } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithEmail:failure", task.exception)
            Toast.makeText(baseContext, "Authentication failed.",
                Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToLogin(){
        val intent = Intent(this, LoginEmailActivity::class.java)
        startActivity(intent)
    }

    private fun addUserToDB(currentUser : FirebaseUser, username : String) {
            val db = Firebase.firestore

            val user = hashMapOf(
                "email" to currentUser.email,
                "displayName" to username
            )

        db.collection("users").document(currentUser.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                goToLogin()
                Toast.makeText(
                    baseContext, "User correctly registered",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

}