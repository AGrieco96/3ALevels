package com.A3Levels.graphics
import android.graphics.Color

/**
 * Some color static methods so I can setup the color quickly and not think hard either.
 */
object myColor {
    fun red(): FloatArray {
        return floatArrayOf(
            Color.red(Color.RED) / 255f,
            Color.green(Color.RED) / 255f,
            Color.blue(Color.RED) / 255f,
            1.0f
        )
    }

    fun green(): FloatArray {
        return floatArrayOf(
            Color.red(Color.GREEN) / 255f,
            Color.green(Color.GREEN) / 255f,
            Color.blue(Color.GREEN) / 255f,
            1.0f
        )
    }

    fun blue(): FloatArray {
        return floatArrayOf(
            Color.red(Color.BLUE) / 255f,
            Color.green(Color.BLUE) / 255f,
            Color.blue(Color.BLUE) / 255f,
            1.0f
        )
    }

    fun yellow(): FloatArray {
        return floatArrayOf(
            Color.red(Color.YELLOW) / 255f,
            Color.green(Color.YELLOW) / 255f,
            Color.blue(Color.YELLOW) / 255f,
            1.0f
        )
    }

    fun cyan(): FloatArray {
        return floatArrayOf(
            Color.red(Color.CYAN) / 255f,
            Color.green(Color.CYAN) / 255f,
            Color.blue(Color.CYAN) / 255f,
            1.0f
        )
    }

    fun gray(): FloatArray {
        return floatArrayOf(
            Color.red(Color.GRAY) / 255f,
            Color.green(Color.GRAY) / 255f,
            Color.blue(Color.GRAY) / 255f,
            1.0f
        )
    }
}