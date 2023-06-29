package com.A3Levels.graphics

import android.app.ActivityManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.A3Levels.HomeActivity
import com.A3Levels.databinding.ActivityPyramid2Binding
import com.A3Levels.game.GameLevelActivity
import com.A3Levels.game.gameLevelExtraInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.checkerframework.common.returnsreceiver.qual.This

class PyramidActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPyramid2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPyramid2Binding.inflate(layoutInflater)

        if (detectOpenGLES30()) {
            //so we know it a opengl 3.0 and use our extended GLsurfaceview.
            //setContentView(myGlSurfaceView(this))
            val layout = binding.pyramid
            layout.addView(myGlSurfaceView(this))
            setContentView(binding.root)
            displayWinnerUI()

        } else {
            // This is where you could create an OpenGL ES 2.0 and/or 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...")
            finish()
        }
        binding.buttonHome.setOnClickListener {
            goHome()
        }
    }



    // Execution flow function
    private fun goHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
    //UI Function

    private val gameExtraInfo: gameLevelExtraInfo = gameLevelExtraInfo()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private fun displayWinnerUI(){
        coroutineScope.launch {
            val counterValues =  gameExtraInfo.retrieveCounter(gameLevelExtraInfo.myLobbyID, gameLevelExtraInfo.myUsername)
            val player1Counter = counterValues.player1Counter
            val player2Counter = counterValues.player2Counter
            println("player1Counter "+player1Counter + " Player2Counter "+player2Counter)
            binding.textScore.text = "$player1Counter-$player2Counter"
            //binding.textCounterP1.text = player1Counter.toString()
            //binding.textCounterP2.text = player2Counter.toString()
            if (player1Counter > player2Counter) {
                //player_1 is always the player who is acting on the telephone
                //binding.result.text = "WINNER"
                //gameLevelExtraInfo.setFlagVictory(true)
                binding.result.text = "WINNER"
            } else {
                //binding.result.text = "LOSER"
                //gameLevelExtraInfo.setFlagVictory(false)
                binding.result.text = "YOU LOSE"
            }
        }
    }


    //OpenGL Function
    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

}