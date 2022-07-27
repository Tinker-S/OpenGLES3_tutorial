package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.opengles.common.MatrixState
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGlSurfaceView(context: Context) : GLSurfaceView(context) {
    private var sceneRenderer: SceneRenderer

    init {
        setEGLContextClientVersion(3)
        sceneRenderer = SceneRenderer()
        setRenderer(sceneRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    inner class SceneRenderer : Renderer {
        var cube: Cube? = null

        override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
            GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
            GLES30.glEnable(GLES30.GL_CULL_FACE)
            GLES30.glEnable(GLES30.GL_DEPTH_TEST)
            cube = Cube(context)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES30.glViewport(0, 0, width, height)
            val ratio = width.toFloat() / height
            MatrixState.setProjectFrustum(-ratio, ratio, -1f, 1f, 3f, 20f)
            MatrixState.setCamera(0f, 5f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)
            MatrixState.setInitialState()
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
            cube?.drawSelf()
        }
    }
}