package com.example.opengles.tutorial

import android.content.Context
import com.example.opengles.common.ShaderUtil.loadFromAssetsFile
import com.example.opengles.common.ShaderUtil.createProgram
import kotlin.jvm.JvmOverloads
import android.opengl.GLSurfaceView
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.graphics.SurfaceTexture
import androidx.camera.core.SurfaceRequest
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30
import android.opengl.GLES11Ext
import android.util.AttributeSet
import android.view.Surface
import androidx.camera.core.Preview
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig

class GLCameraView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs), GLSurfaceView.Renderer, OnFrameAvailableListener {

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var programId = 0
    private var textureId = 0
    private var surfaceTexture: SurfaceTexture? = null
    private val textureTransformMatrix = FloatArray(16)

    private var vertexBuffer: FloatBuffer? = null
    private var coordinateBuffer: FloatBuffer? = null
    private var indexBuffer: ByteBuffer? = null

    fun attachPreview(preview: Preview) {
        preview.setSurfaceProvider { request: SurfaceRequest ->
            val resolution = request.resolution
            // 重要！！！不设置的话预览将会非常模糊
            surfaceTexture!!.setDefaultBufferSize(resolution.width, resolution.height)
            val surface = Surface(surfaceTexture)
            request.provideSurface(surface, executor) {
                surface.release()
                surfaceTexture!!.release()
            }
        }
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        val ids = IntArray(1)
        GLES30.glGenTextures(1, ids, 0)
        textureId = ids[0]
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture!!.setOnFrameAvailableListener(this)

        val vertexShader = loadFromAssetsFile("vertex.glsl", resources)
        val fragmentShader = loadFromAssetsFile("frag.glsl", resources)
        programId = createProgram(vertexShader, fragmentShader)
        val vertices = floatArrayOf(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
        )
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer?.put(vertices)
        vertexBuffer?.position(0)
        val indexes = byteArrayOf(
            0, 1, 2,
            2, 3, 0
        )
        val ibb = ByteBuffer.allocateDirect(indexes.size * 4)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb
        indexBuffer?.put(indexes)
        indexBuffer?.position(0)

        // 这里是顺时针旋转90度后的纹理坐标映射
        val textureCoordinate = floatArrayOf(
            1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f
        )
        val tbb = ByteBuffer.allocateDirect(textureCoordinate.size * 4)
        tbb.order(ByteOrder.nativeOrder())
        coordinateBuffer = tbb.asFloatBuffer()
        coordinateBuffer?.put(textureCoordinate)
        coordinateBuffer?.position(0)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES30.glClearColor(1f, 0f, 0f, 0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        // 更新纹理
        surfaceTexture!!.updateTexImage()
        surfaceTexture!!.getTransformMatrix(textureTransformMatrix)

        GLES30.glUseProgram(programId)

        GLES30.glUniformMatrix4fv(0, 1, false, textureTransformMatrix, 0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, coordinateBuffer)
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_BYTE, indexBuffer)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        requestRender()
    }

    init {
        setEGLContextClientVersion(3)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}