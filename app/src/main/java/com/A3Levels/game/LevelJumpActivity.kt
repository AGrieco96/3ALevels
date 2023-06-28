package com.A3Levels.game

import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.A3Levels.R
import com.A3Levels.databinding.ActivityHomeBinding
import com.A3Levels.databinding.ActivityLevelJumpBinding
import com.A3Levels.databinding.ActivityPhotoLevelBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class LevelJumpActivity : AppCompatActivity() {

    private lateinit var audioRecord: AudioRecord
    //private lateinit var imageView: ImageView
    private lateinit var imageView : FrameLayout
    private var isRecording = false
    private lateinit var binding: ActivityLevelJumpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_level_jump)

        binding = ActivityLevelJumpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Request the RECORD_AUDIO permission if it is not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), 1
            )
        } else {
            // Permission is already granted, initialize AudioRecord and start recording
            initializeAudioRecord()
            startRecording()
        }

        //binding.niagara.visibility = View.INVISIBLE

        //imageView = findViewById(R.id.characterImageView)
        imageView = findViewById(R.id.shipLayout)
        messageListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission has been granted, initialize AudioRecord and start recording
            initializeAudioRecord()
            startRecording()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeAudioRecord() {
        // Determine the minimum buffer size for AudioRecord
        val minBufferSize = AudioRecord.getMinBufferSize(
            44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, minBufferSize
        )
    }

    private fun startRecording() {
        audioRecord.startRecording()
        isRecording = true
        var threshold = 1000

        Thread {
            val buffer = ShortArray(audioRecord.bufferSizeInFrames)
            while (isRecording) {

                audioRecord.read(buffer, 0, buffer.size)
                val amplitude = buffer.maxOrNull() ?: 0

                if (amplitude > threshold) {
                    runOnUiThread {
                        if ( binding.niagara.visibility == View.VISIBLE ) {
                            //nothing
                        } else {
                            println("FIRE VISIBLE!")
                            binding.niagara.visibility = View.VISIBLE
                        }
                        imageView.translationY -= amplitude/1000;
                        println(imageView.y)
                    }
                }else {

                    if ( binding.niagara.visibility == View.INVISIBLE ) {
                        //nothing
                    } else {
                        println("Fire disabled")
                        binding.niagara.visibility = View.INVISIBLE
                    }
                }

                if(imageView.y < 100.0F) {
                    isRecording = false
                    println("SEI ARRIVATO")
                    endGame()
                }
            }
        }.start()


    }

    fun endGame(){
        // End of GameLogic , so come back to the GameLevelActivity, for the sake of the execution flow
        val intent = Intent(this, GameLevelActivity::class.java)

        // Gli dovremmo passare alcuni parametri, come se deve visualizzare o meno lo start o la fine del tutorial.
        // intent.putExtra("username", username) - # del livello etc.
        gameLevelExtraInfo.setlLevel(4)
        gameLevelExtraInfo.setFlag(false)
        gameLevelExtraInfo.setLobbyId(gameLevelExtraInfo.myLobbyID)
        listener?.remove()
        listener = null
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()

        // Stop recording audio and release resources
        audioRecord.stop()
        audioRecord.release()
        isRecording = false
    }

    private var listener: ListenerRegistration? = null
    fun messageListener(){
        val db = FirebaseFirestore.getInstance()
        val username = gameLevelExtraInfo.myUsername
        println("Retrieve dell'username dall'activity photo level : "+ username)
        val docMessagesRef = db.collection("messages").document(username)
        listener = docMessagesRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@EventListener
            }
            println("Snapshot attuale" + snapshot)

            if (snapshot != null && snapshot.exists()) {

                val myfield = snapshot.getString("lastMessage")
                if ( !(myfield.equals("Chat created!")) ){
                    Toast.makeText(
                        baseContext, myfield,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Log.d(ContentValues.TAG, "Current data: null")
                //Log.d(TAG, "Document ID: " + docLobbyRef.id)
            }
        })
    }
}

/*
*     private fun setupListener(lobbyId: String){

        FirebaseApp.initializeApp(this)
        val db = FirebaseFirestore.getInstance()
        val docGameRef = db.collection("games").document(lobbyId)
        docGameRef.addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            if (snapshot != null && snapshot.exists()) {
                val myLevel = snapshot.get("level").toString()
                Log.d(TAG, "Current data: ${snapshot.data}")
                Log.d(TAG, "Current data: $myLevel")
                if (myLevel.equals("2")) {
                    val intent = Intent(this, BallActivity::class.java)
                    startActivity(intent)
                }
            } else {
                Log.d(TAG, "Current data: null")
                //Log.d(TAG, "Document ID: " + docLobbyRef.id)
            }
        })



    }
* */