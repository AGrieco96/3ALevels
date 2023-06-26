package com.A3Levels.game

class Level_2(private val levelCallback: LevelCallback) : Level_0() {
    fun startLevel(){
        println("Livello 2 Startato.")
        levelCallback.onLevelCompleted()
    }


    interface LevelCallback {
        fun onLevelCompleted()
    }
}