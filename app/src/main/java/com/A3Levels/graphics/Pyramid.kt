package com.A3Levels.graphics

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class Pyramid {
    private val mProgramObject: Int
    private var mMVPMatrixHandle = 0
    private var mColorHandle = 0
    private val mVertices: FloatBuffer

    //initial size of the pyramid.  set here, so it is easier to change later.
    var size = 0.4f

    //this is the initial data, which will need to translated into the mVertices variable in the consturctor.
    var mVerticesData =
        floatArrayOf( ////////////////////////////////////////////////////////////////////
            // top
            ////////////////////////////////////////////////////////////////////
            // FRONT
            0.0f, size, 0.0f,  // top
            -size, -size, size,  // front-left
            size, -size, size,  // front-right
            // RIGHT
            0.0f, size, 0.0f,  // top
            size, -size, size,  // front-right
            size, -size, -size,  // back-right
            // BACK
            0.0f, size, 0.0f,  // top
            size, -size, -size,  // back-right
            -size, -size, -size,  // back-left
            // LEFT
            0.0f, size, 0.0f,  // top
            -size, -size, -size,  // back-left
            -size, -size, size,  // front-left
            ////////////////////////////////////////////////////////////////////
            // BOTTOM
            ////////////////////////////////////////////////////////////////////
            // Triangle 1
            -size, -size, -size,  // back-left
            -size, -size, size,  // front-left
            size, -size, size,  // front-right
            // Triangle 2
            size, -size, size,  // front-right
            size, -size, -size,  // back-right
            -size, -size, -size // back-left
        )
    var colorcyan = myColor.cyan()
    var colorblue = myColor.blue()
    var colorred = myColor.red()
    var colorgray = myColor.gray()
    var colorgreen = myColor.green()
    var coloryellow = myColor.yellow()

    //vertex shader code
    var vShaderStr = """#version 300 es 			  
        uniform mat4 uMVPMatrix;     
        in vec4 vPosition;           
        void main()                  
        {                            
           gl_Position = uMVPMatrix * vPosition;  
        }                            
        """

    //fragment shader code.
    var fShaderStr = """#version 300 es		 			          	
        precision mediump float;					  	
        uniform vec4 vColor;	 			 		  	
        out vec4 fragColor;	 			 		  	
        void main()                                  
        {                                            
          fragColor = vColor;                    	
        }                                            
        """
    var TAG = "Pyramid"

    //finally some methods
    //constructor
    init {
        //first setup the mVertices correctly.
        mVertices = ByteBuffer
            .allocateDirect(mVerticesData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(mVerticesData)
        mVertices.position(0)

        //setup the shaders
        val vertexShader: Int
        val fragmentShader: Int
        val programObject: Int
        val linked = IntArray(1)

        // Load the vertex/fragment shaders
        vertexShader = myRenderer.LoadShader(GLES30.GL_VERTEX_SHADER, vShaderStr)
        fragmentShader = myRenderer.LoadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr)

        // Create the program object
        programObject = GLES30.glCreateProgram()
        if (programObject == 0) {
            Log.e(TAG, "Pyramid.kt : error on glCreateProgram?")
        }
        GLES30.glAttachShader(programObject, vertexShader)
        GLES30.glAttachShader(programObject, fragmentShader)

        // Bind vPosition to attribute 0
        GLES30.glBindAttribLocation(programObject, 0, "vPosition")

        // Link the program
        GLES30.glLinkProgram(programObject)

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0)
        if (linked[0] == 0) {
            Log.e(TAG, "Pyramid.kt : error linking program:")
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject))
            GLES30.glDeleteProgram(programObject)
        }

        // Store the program object
        mProgramObject = programObject

    }

    fun draw(mvpMatrix: FloatArray?) {

        // Use the program object
        GLES30.glUseProgram(mProgramObject)

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgramObject, "uMVPMatrix")
        myRenderer.checkGlError("glGetUniformLocation")

        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgramObject, "vColor")


        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        myRenderer.checkGlError("glUniformMatrix4fv")
        val VERTEX_POS_INDX = 0
        mVertices.position(VERTEX_POS_INDX) //just in case.  We did it already though.

        //add all the points to the space, so they can be correct by the transformations.
        //would need to do this even if there were no transformations actually.
        GLES30.glVertexAttribPointer(
            VERTEX_POS_INDX, 3, GLES30.GL_FLOAT,
            false, 0, mVertices
        )
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX)


        //Now we are ready to draw the cube finally.
        var startPos = 0
        val verticesPerface = 3

        //draw front face
        GLES30.glUniform4fv(mColorHandle, 1, colorblue, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface)
        startPos += verticesPerface

        //draw right face
        GLES30.glUniform4fv(mColorHandle, 1, colorcyan, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface)
        startPos += verticesPerface

        //draw back face
        GLES30.glUniform4fv(mColorHandle, 1, colorred, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface)
        startPos += verticesPerface

        //draw left face
        GLES30.glUniform4fv(mColorHandle, 1, colorgray, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface)
        startPos += verticesPerface


        //draw bottom face 1 tri square, so 6 faces.
        GLES30.glUniform4fv(mColorHandle, 1, coloryellow, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface)
        startPos += verticesPerface

        //draw bottom face 2 tri square, so 6 faces.
        GLES30.glUniform4fv(mColorHandle, 1, coloryellow, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, startPos, verticesPerface)
        startPos += verticesPerface
        //last face, so no need to increment.
    }
}
