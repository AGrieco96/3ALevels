package com.A3Levels.game

class Level_3(private val levelCallback: LevelCallback) : Level_0() {
    fun startLevel(){
        println("Livello 3 Startato.")
        levelCallback.onLevelCompleted()
    }


    interface LevelCallback {
        fun onLevelCompleted()
    }
}