package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import com.example.opengles.common.ShaderUtil
import com.example.opengles.common.TextureUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 绘制多个纹理
 * <p/>
 * 坐标对应关系
 * 顶点坐标：左上角:-1,1，右上角:1,1，左下角:-1,-1，右下角：1,-1
 * 纹理坐标：左上角:0,0   右上角:1,0，左下角:0,1   右下角:1,1
 * <p/>
 * 一一对应就是正常的纹理绘制，改变映射关系可以实现：旋转，翻转，镜像等简单变换
 */
class MultiTextureRectangle(context: Context) {
    private var program: Int = -1
    private var texture1Handle = -1
    private var texture2Handle = -1
    private var textureIds = IntArray(2)

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ByteBuffer
    private lateinit var textCoordinateBuffer1: FloatBuffer
    private lateinit var textCoordinateBuffer2: FloatBuffer
    private val textureCoordinate1 = floatArrayOf(
        0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f
    )
    private val textureCoordinate2 = floatArrayOf(
        -0.5f, 1.5f, 1.5f, 1.5f, 1.5f, -0.5f, -0.5f, -0.5f
    )

    init {
        initVertexData()
        initShader(context)
        initTexture(context)
    }

    private fun initVertexData() {
        val vertices = floatArrayOf(
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
        )
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val indexes = byteArrayOf(
            0, 1, 2,
            2, 3, 0,
        )
        val ibb = ByteBuffer.allocateDirect(indexes.size * 4)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb
        indexBuffer.put(indexes)
        indexBuffer.position(0)

        var tbb = ByteBuffer.allocateDirect(textureCoordinate1.size * 4)
        tbb.order(ByteOrder.nativeOrder())
        textCoordinateBuffer1 = tbb.asFloatBuffer()
        textCoordinateBuffer1.put(textureCoordinate1)
        textCoordinateBuffer1.position(0)

        tbb = ByteBuffer.allocateDirect(textureCoordinate2.size * 4)
        tbb.order(ByteOrder.nativeOrder())
        textCoordinateBuffer2 = tbb.asFloatBuffer()
        textCoordinateBuffer2.put(textureCoordinate2)
        textCoordinateBuffer2.position(0)
    }

    private fun initShader(context: Context) {
        val vertexShader = ShaderUtil.loadFromAssetsFile("vertex_m.glsl", context.resources)
        val fragmentShader = ShaderUtil.loadFromAssetsFile("frag_m.glsl", context.resources)
        program = ShaderUtil.createProgram(vertexShader, fragmentShader)
        texture1Handle = GLES30.glGetUniformLocation(program, "sTexture1")
        texture2Handle = GLES30.glGetUniformLocation(program, "sTexture2")
    }

    private fun initTexture(context: Context) {
        GLES30.glGenTextures(textureIds.size, textureIds, 0)
        TextureUtil.loadTexture(context, textureIds[0], R.drawable.lena)
        TextureUtil.loadTexture(context, textureIds[1], R.drawable.jay)
    }

    fun drawSelf() {
        GLES30.glUseProgram(program)

        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, textCoordinateBuffer1)
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(2, 2, GLES30.GL_FLOAT, false, 0, textCoordinateBuffer2)
        GLES30.glEnableVertexAttribArray(2)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])
        GLES30.glUniform1i(texture1Handle, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[1])
        GLES30.glUniform1i(texture2Handle, 1)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_BYTE, indexBuffer)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
        GLES30.glDisableVertexAttribArray(2)
    }
}