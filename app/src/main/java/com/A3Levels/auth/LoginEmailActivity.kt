package com.A3Levels.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.MainActivity
import com.A3Levels.databinding.LoginEmailBinding
import com.google.firebase.auth.FirebaseAuth

class LoginEmailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: LoginEmailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginEmailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title="Login"

        auth= FirebaseAuth.getInstance()
    }

    fun login(view: View){
        val email= binding.editTextEmailAddress.text.toString()
        val password= binding.editTextPassword.text.toString()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val intent= Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun goToRegister(view: View){
        val intent= Intent(this,RegisterEmailActivity::class.java)
        startActivity(intent)
    }
}