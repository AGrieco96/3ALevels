package com.A3Levels.game

class gameLevelExtraInfo {
    companion object  {
        // Global Information to handle
        lateinit var myUsername:String
        var myLobbyID:String = "1"
        var myLevel:Int = 0
        var myFlag:Boolean = true

        fun setUsername(username:String){
            myUsername = username.toString()
        }
        fun setLobbyId(lobbyID:String){
            myLobbyID = lobbyID.toString()
        }
        fun setlLevel(level: Int){
            myLevel = level
        }
        fun setFlag(flag:Boolean){
            myFlag = flag
        }

        //Handle the specific case for photoLevel
        lateinit var myObjectInPhoto: String
        lateinit var myImage : String
        fun setObjectInPhoto(objectInPhoto : String){
            myObjectInPhoto = objectInPhoto
        }
        fun setImage ( imageString : String ){
            myImage = imageString
        }

        private var instance: gameLevelExtraInfo? = null
        fun getInstance(): gameLevelExtraInfo {
            if (instance == null) {
                instance = gameLevelExtraInfo()
            }
            return instance as gameLevelExtraInfo
        }


    }
}