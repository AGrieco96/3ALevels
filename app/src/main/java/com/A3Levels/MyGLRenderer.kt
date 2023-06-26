package com.A3Levels

import android.R.attr
import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig


public class MyGLRenderer : GLSurfaceView.Renderer {

    private var mWidth = 0
    private var mHeight = 0
    private val TAG = "myRenderer"
    var mPyramid: Pyramid? = null
    private var mAngle = 0f
    private var mTransY = 0f
    private var mTransX = 0f
    private val Z_NEAR = 1f
    private val Z_FAR = 40f

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)

     init{

     }

    fun LoadShader( type:Int ,shaderSrc:String):Int{
        val shader: Int
        val compiled = IntArray(1)

        // Create the shader object

        // Create the shader object
        shader = GLES30.glCreateShader(attr.type)

        if (shader == 0) {
            return 0
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES30.glCompileShader(shader);

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    fun checkGlError(glOperation: String) {
        var error: Int
        while (GLES30.glGetError().also { error = it } != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "$glOperation: glError $error")
            throw RuntimeException("$glOperation: glError $error")
        }
    }

     override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {
        //set the clear buffer color to light gray.
        //GLES30.glClearColor(0.9f, .9f, 0.9f, 0.9f);
        //set the clear buffer color to a dark gray
        GLES30.glClearColor(0.1f, .1f, 0.1f, 1.0f)
        //initialize the cube code for drawing.
        mPyramid = Pyramid()
        //if we had other objects setup them up here as well.
    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        mWidth = width
        mHeight = height
        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight)
        val aspect = width.toFloat() / height

        // this projection matrix is applied to object coordinates
        //no idea why 53.13f, it was used in another example and it worked.
        Matrix.perspectiveM(mProjectionMatrix, 0, 53.13f, aspect, Z_NEAR, Z_FAR)
    }

    override fun onDrawFrame(glUnused: GL10?) {
        // Clear the color buffer  set above by glClearColor.
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        //need this otherwise, it will over right stuff and the cube will look wrong!
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        // Set the camera position (View matrix)  note Matrix is an include, not a declared method.
        Matrix.setLookAtM(mViewMatrix, 0, 0F, 0F, -3.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Create a rotation and translation for the cube
        Matrix.setIdentityM(mRotationMatrix, 0)

        //move the cube up/down and left/right
        Matrix.translateM(mRotationMatrix, 0, mTransX, mTransY, 0F)

        //mangle is how fast, x,y,z which directions it rotates.
        Matrix.rotateM(mRotationMatrix, 0, mAngle, 0.4f, 1.0f, 0.6f)

        // combine the model with the view matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0)

        // combine the model-view with the projection matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0)
        mPyramid?.draw(mMVPMatrix)

        //change the angle, so the cube will spin.
        mAngle += .4.toFloat()
    }

    //used the touch listener to move the cube up/down (y) and left/right (x)
    open fun getY(): Float {
        return mTransY
    }

    open fun setY(mY: Float) {
        mTransY = mY
    }

    open fun getX(): Float {
        return mTransX
    }

    open fun setX(mX: Float) {
        mTransX = mX
    }


}