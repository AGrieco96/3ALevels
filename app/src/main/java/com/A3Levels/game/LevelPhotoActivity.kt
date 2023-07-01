package com.A3Levels.game

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.A3Levels.databinding.ActivityPhotoLevelBinding
import com.A3Levels.databinding.ActivityTestLevelBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class LevelPhotoActivity : AppCompatActivity(), gameLevelExtraInfo.TimerUpdateListener {

    private lateinit var viewBinding: ActivityPhotoLevelBinding

    private var imageCapture: ImageCapture? = null
    private val objectList = listOf("chair", "bottle", "cellular", "television", "key", "wallet")

    private lateinit var cameraExecutor: ExecutorService
    lateinit var objectInPhoto: String
    private var listener: ListenerRegistration? = null

    // Singleton
    private val gameExtraInfo: gameLevelExtraInfo = gameLevelExtraInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPhotoLevelBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        //Setup
        //objectInPhoto = objectList[(0..5).random()]
        objectInPhoto = "cellular"          // ONLY FOR DEMO PURPOSE
        viewBinding.objectToSearch.setText(objectInPhoto.toString())


        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()

        messageListener()

        // Timer Handle
        gameExtraInfo.setTimerUpdateListener(this)
        gameExtraInfo.startTimer()
    }

    // Update UI
    override fun onTimerUpdate(minutes: Int, seconds: Int, milliseconds: Int) {
        viewBinding.timerTextView3.text = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
    }
    override fun onTimerFinished(seconds: Int, milliseconds: Int) {
        val finalTime = ((seconds * 1000) + milliseconds).toString()
        println("finalTime $finalTime")
        gameLevelExtraInfo.setmyTime(finalTime)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.ITALY)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    imageBase64(output.savedUri)
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().setTargetResolution(Size(800,600)).build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }

    private fun imageBase64(uri: Uri?) {
        val inputStream = uri?.let { contentResolver.openInputStream(it) }

        val bytes: ByteArray
        val buffer = ByteArray(8192)
        var bytesRead: Int
        val output = ByteArrayOutputStream()

        try {
            if (inputStream != null) {
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        bytes = output.toByteArray()
        return sendPhoto(Base64.encodeToString(bytes, Base64.DEFAULT))
    }

    private fun sendPhoto(imageString : String) {

        // Set the new variable to share
        gameLevelExtraInfo.setlLevel(2)
        gameLevelExtraInfo.setFlag(false)
        gameLevelExtraInfo.setObjectInPhoto(objectInPhoto)
        gameLevelExtraInfo.setImage(imageString)
        gameLevelExtraInfo.setLobbyId(gameLevelExtraInfo.myLobbyID)
        gameLevelExtraInfo.setUsername(gameLevelExtraInfo.myUsername)
        listener?.remove()
        listener = null

        //stop timer.
        gameExtraInfo.stopTimer()

        val intent = Intent(this, GameLevelActivity::class.java)
        startActivity(intent)

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

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