package com.A3Levels.game

class Level_1(private val levelCallback: LevelCallback) : Level_0() {
    fun startLevel(){
        println("Livello 1 Startato.")
        levelCallback.onLevelCompleted()
    }


    interface LevelCallback {
        fun onLevelCompleted()
    }
}