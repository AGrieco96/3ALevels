package com.A3Levels.graphics

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class myGlSurfaceView(context: Context?) : GLSurfaceView(context) {
    var myRender: myRenderer
    private var mPreviousX = 0f
    private var mPreviousY = 0f

    init {
        // Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3)
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        // Set the Renderer for drawing on the GLSurfaceView
        myRender = myRenderer(context)
        setRenderer(myRender)

        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_CONTINUOUSLY
    }


    companion object {
        //private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private const val TOUCH_SCALE_FACTOR = 0.015f
    }
}