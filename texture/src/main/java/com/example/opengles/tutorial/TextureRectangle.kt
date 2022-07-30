package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import androidx.annotation.DrawableRes
import com.example.opengles.common.ShaderUtil
import com.example.opengles.common.TextureUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 纹理矩形
 * <p/>
 * 坐标对应关系
 * 顶点坐标：左上角:-1,1，右上角:1,1，左下角:-1,-1，右下角：1,-1
 * 纹理坐标：左上角:0,0   右上角:1,0，左下角:0,1   右下角:1,1
 * <p/>
 * 一一对应就是正常的纹理绘制，改变映射关系可以实现：旋转，翻转，镜像等简单变换
 */
class TextureRectangle(context: Context, @DrawableRes val textureRes: Int) {
    private var program: Int = -1
    private var textureIds = IntArray(1)

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ByteBuffer
    private lateinit var textCoordinateBuffer: FloatBuffer
    private val textureCoordinate = TEXTURE_NO_ROTATION

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

        val tbb = ByteBuffer.allocateDirect(textureCoordinate.size * 4)
        tbb.order(ByteOrder.nativeOrder())
        textCoordinateBuffer = tbb.asFloatBuffer()
        textCoordinateBuffer.put(textureCoordinate)
        textCoordinateBuffer.position(0)
    }

    private fun initShader(context: Context) {
        val vertexShader = ShaderUtil.loadFromAssetsFile("vertex_s.glsl", context.resources)
        val fragmentShader = ShaderUtil.loadFromAssetsFile("frag_s.glsl", context.resources)
        program = ShaderUtil.createProgram(vertexShader, fragmentShader)
    }

    private fun initTexture(context: Context) {
        GLES30.glGenTextures(1, textureIds, 0)
        TextureUtil.loadTexture(context, textureIds[0], textureRes)
    }

    fun drawSelf() {
        GLES30.glUseProgram(program)

        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 0, textCoordinateBuffer)
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_BYTE, indexBuffer)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
    }

    companion object {
        private val TEXTURE_NO_ROTATION = floatArrayOf(
            0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f
        )
    }
}