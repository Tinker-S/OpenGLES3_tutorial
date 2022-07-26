package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.min

class MyGlSurfaceView(context: Context) : GLSurfaceView(context) {
    private var sceneRenderer: SceneRenderer

    init {
        setEGLContextClientVersion(3)
        sceneRenderer = SceneRenderer()
        setRenderer(sceneRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    inner class SceneRenderer : Renderer {
        var star: Star? = null

        override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
            GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
            star = Star(context)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            // 暂时没有引入变换矩阵，为了让图不变形，我们先手动将viewport设置成正方形
            val minSize = min(width, height)
            GLES30.glViewport(0, 0, minSize, minSize)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
            star?.drawSelf()
        }
    }
}