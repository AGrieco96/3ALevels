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
import android.media.AudioFormat
import android.media.AudioRecord
import android.view.View
import android.widget.FrameLayout
import com.A3Levels.R
import com.A3Levels.databinding.ActivityHomeBinding
import com.A3Levels.databinding.ActivityLevelJumpBinding
import com.A3Levels.databinding.ActivityPhotoLevelBinding

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
                }
            }
        }.start()


    }


    override fun onDestroy() {
        super.onDestroy()

        // Stop recording audio and release resources
        audioRecord.stop()
        audioRecord.release()
        isRecording = false
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