package com.A3Levels

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;


class MyGLSurfaceView(context: Context) : GLSurfaceView(context){

    private var myRender: MyGLRenderer? = null


    init {

        // Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3)
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        // Set the Renderer for drawing on the GLSurfaceView
        myRender = MyGLRenderer()
        setRenderer(myRender)

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
    }



    //private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private val TOUCH_SCALE_FACTOR = 0.015f
    private var mPreviousX = 0f
    private var mPreviousY = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        val x = e.x
        val y = e.y
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mPreviousX
                //subtract, so the cube moves the same direction as your finger.
                //with plus it moves the opposite direction.
                myRender?.setX(myRender!!.getX() - dx * TOUCH_SCALE_FACTOR)
                val dy = y - mPreviousY
                myRender?.setY(myRender!!.getY() - dy * TOUCH_SCALE_FACTOR)
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }
}