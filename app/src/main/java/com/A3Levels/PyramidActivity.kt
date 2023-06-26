package com.A3Levels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.util.Log
import android.view.View

class PyramidActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (detectOpenGLES30()) {
            // sappiamo che è OpenGL 3.0, quindi utilizziamo la nostra GLSurfaceView estesa
            setContentView(MyGLSurfaceView(this))
        } else {
            // Qui potresti creare un renderer compatibile con OpenGL ES 2.0 e/o 1.x
            // se desideri supportare entrambe le versioni ES 1 e ES 2.
            Log.e("openglcube", "OpenGL ES 3.0 non supportato sul dispositivo. Uscita...")
            finish()
        }
    }


    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info: ConfigurationInfo = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }
}