package com.example.opengles.tutorial

import android.annotation.SuppressLint
import android.content.Context
import com.example.opengles.common.MatrixState.pushMatrix
import com.example.opengles.common.MatrixState.translate
import com.example.opengles.common.MatrixState.rotate
import com.example.opengles.common.MatrixState.popMatrix
import com.example.opengles.common.MatrixState.setProjectFrustum
import com.example.opengles.common.MatrixState.setCamera
import com.example.opengles.common.MatrixState.setInitialState
import com.example.opengles.common.MatrixState.setLightLocation
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30
import com.example.opengles.common.MatrixState
import javax.microedition.khronos.egl.EGLConfig

internal class MyGlSurfaceView(context: Context?) : GLSurfaceView(context) {
     //角度缩放比例
    private val TOUCH_SCALE_FACTOR = 180.0f / 320
    private val mRenderer: SceneRenderer
    private var mPreviousY = 0f
    private var mPreviousX = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val y = e.y
        val x = e.x
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dy = y - mPreviousY
                val dx = x - mPreviousX
                mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR
                mRenderer.xAngle += dy * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }
        mPreviousY = y
        mPreviousX = x
        return true
    }

    private inner class SceneRenderer : Renderer {
        var yAngle = 0f
        var xAngle = 0f

        var lovo: LoadedObjectVertexNormal? = null

        override fun onDrawFrame(gl: GL10) {
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)

            pushMatrix()
            translate(0f, -2f, -25f)
            MatrixState.scale(0.7f, 0.7f, 0.7f)
            rotate(yAngle, 0f, 1f, 0f)
            rotate(xAngle, 1f, 0f, 0f)
            lovo?.drawSelf()
            popMatrix()
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            GLES30.glViewport(0, 0, width, height)
            val ratio = width.toFloat() / height
            setProjectFrustum(-ratio, ratio, -1f, 1f, 2f, 100f)
            setCamera(0f, 0f, 0f, 0f, 0f, -1f, 0f, 1.0f, 0.0f)
        }

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            GLES30.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
            GLES30.glEnable(GLES30.GL_DEPTH_TEST)
            GLES30.glDisable(GLES30.GL_CULL_FACE)
            setInitialState()
            //初始化光源位置
            setLightLocation(40f, 10f, 20f)
            //加载要绘制的物体
            lovo = LoadUtil.loadFromFile("ch.obj", this@MyGlSurfaceView.resources, context)
        }
    }

    init {
        setEGLContextClientVersion(3)
        mRenderer = SceneRenderer()
        setRenderer(mRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}