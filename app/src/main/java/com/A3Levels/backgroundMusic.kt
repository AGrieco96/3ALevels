package com.A3Levels

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class BackgroundMusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.makeba) //
        mediaPlayer?.isLooping = true // Ripeti la canzone in modo continuo
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer?.start() // Avvia la riproduzione della musica
        return START_STICKY // Il servizio verrà riavviato automaticamente se viene terminato dal sistema
    }

    override fun onDestroy() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningActivities = activityManager.runningAppProcesses

        if (runningActivities.size <= 1) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
