package com.A3Levels.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.A3Levels.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GithubSignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val provider = github()
        signInWithProvider(provider)
    }

    private fun github(): OAuthProvider.Builder {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.addCustomParameter("login", "your-email@gmail.com")
        provider.scopes = listOf("user:email")
        return provider
    }

    private fun getPendingAuthResult() {
        val pendingResultTask = auth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    // User is signed in.
                    // IdP data available in
                    // authResult.getAdditionalUserInfo().getProfile().
                    // The OAuth access token can also be retrieved:
                    // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                    // The OAuth secret can be retrieved by calling:
                    // ((OAuthCredential)authResult.getCredential()).getSecret().
                }
                .addOnFailureListener {
                    // Handle failure.
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
        }
        // [END auth_oidc_pending_result]
    }

    private fun signInWithProvider(provider: OAuthProvider.Builder) {
        // [START auth_oidc_provider_signin]
        auth
            .startActivityForSignInWithProvider( /* activity = */this, provider.build())
            .addOnSuccessListener {
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                // The OAuth secret can be retrieved by calling:
                // ((OAuthCredential)authResult.getCredential()).getSecret().
                val userFirebase = auth.currentUser
                if(userFirebase != null) {
                    addUserToDB(userFirebase)
                }            }
            .addOnFailureListener {
                // Handle failure.
            }
        // [END auth_oidc_provider_signin]
    }

    private fun linkWithProvider(provider: OAuthProvider.Builder) {
        // [START auth_oidc_provider_link]
        // The user is already signed-in.
        val firebaseUser = auth.currentUser!!
        firebaseUser
            .startActivityForLinkWithProvider( /* activity = */this, provider.build())
            .addOnSuccessListener {
                // Provider credential is linked to the current user.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // authResult.getCredential().getAccessToken().
                // The OAuth secret can be retrieved by calling:
                // authResult.getCredential().getSecret().
            }
            .addOnFailureListener {
                // Handle failure.
            }
        // [END auth_oidc_provider_link]
    }

    private fun reauthenticateWithProvider(provider: OAuthProvider.Builder) {
        // [START auth_oidc_provider_reauth]
        // The user is already signed-in.
        val firebaseUser = auth.currentUser!!
        firebaseUser
            .startActivityForReauthenticateWithProvider( /* activity = */this, provider.build())
            .addOnSuccessListener {
                // User is re-authenticated with fresh tokens and
                // should be able to perform sensitive operations
                // like account deletion and email or password
                // update.
            }
            .addOnFailureListener {
                // Handle failure.
            }
        // [END auth_oidc_provider_reauth]
    }

    private fun goToHome(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun addUserToDB(currentUser : FirebaseUser) {
        val db = Firebase.firestore

        val user = hashMapOf(
            "email" to currentUser.email,
            "displayName" to currentUser.displayName
        )

        db.collection("users").document(currentUser.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                goToHome()
                Toast.makeText(
                    baseContext, "User logged with Github",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

}