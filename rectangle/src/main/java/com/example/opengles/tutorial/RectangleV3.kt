package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import com.example.opengles.common.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 矩形（使用glDrawElements绘制）
 */
class RectangleV3(context: Context) {
    private var program: Int = 0

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer
    private lateinit var indexBuffer: ByteBuffer

    init {
        initVertexData()
        initShader(context)
    }

    private fun initVertexData() {
        val vertices = floatArrayOf(
            -0.5f, -0.8f, 0.0f,
            0.5f, -0.8f, 0.0f,
            0.5f, -0.3f, 0.0f,
            -0.5f, -0.3f, 0.0f,
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

        val colors = floatArrayOf(
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 1f, 1f, 1f,
        )
        val cbb = ByteBuffer.allocateDirect(colors.size * 4)
        cbb.order(ByteOrder.nativeOrder())
        colorBuffer = cbb.asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)
    }

    private fun initShader(context: Context) {
        val vertexShader = ShaderUtil.loadFromAssetsFile("vertex.glsl", context.resources)
        val fragmentShader = ShaderUtil.loadFromAssetsFile("frag.glsl", context.resources)
        program = ShaderUtil.createProgram(vertexShader, fragmentShader)
    }

    fun drawSelf() {
        GLES30.glUseProgram(program)

        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glEnableVertexAttribArray(1)

        // 6为顶点索引的个数
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_BYTE, indexBuffer)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }

}