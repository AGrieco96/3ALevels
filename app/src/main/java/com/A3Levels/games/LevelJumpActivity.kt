package com.A3Levels.games

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
import com.A3Levels.R
import java.lang.Math.abs

class LevelJumpActivity : AppCompatActivity() {

    private lateinit var audioRecord: AudioRecord
    private lateinit var imageView: ImageView
    private var isRecording = false
    private var lastAmplitude = 0
    private var jumpHeight = 0
    private var isJumping = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_jump)

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

        imageView = findViewById(R.id.characterImageView)
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
        var threshold = 5000
        var jumpDuration = 800 // The duration of the jump, in milliseconds
        var jumpStartTime = 0L // The time at which the jump started
        var isJumping = false // Whether or not the character is currently jumping

        Thread {
            val buffer = ShortArray(audioRecord.bufferSizeInFrames)
            while (isRecording) {
                audioRecord.read(buffer, 0, buffer.size)
                val amplitude = buffer.maxOrNull() ?: 0

                if (amplitude > threshold && !isJumping) {
                    isJumping = true
                    jumpStartTime = System.currentTimeMillis()
                }

                if (isJumping) {
                    val elapsedTime = System.currentTimeMillis() - jumpStartTime
                    val jumpHeight = (Math.sin(elapsedTime.toDouble() / jumpDuration * Math.PI) * 100).toInt()
                    runOnUiThread {
                        imageView.translationY = jumpHeight.toFloat()
                    }

                    if (elapsedTime >= jumpDuration) {
                        isJumping = false
                        runOnUiThread {
                            imageView.translationY = 0F
                        }
                    }
                } else {
                    runOnUiThread {
                        imageView.translationY = 0F
                    }
                }

                Thread.sleep(16)
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