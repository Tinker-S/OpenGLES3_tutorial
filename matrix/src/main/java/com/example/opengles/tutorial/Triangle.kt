package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import com.example.opengles.common.MatrixState
import com.example.opengles.common.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 三角形
 * <p />
 * 矩阵变换：最终变换矩阵mvpMatrix = projMatrix * viewMatrix * modelMatrix
 */
class Triangle(context: Context) {
    private var program: Int = 0
    private var angle = 0f

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer

    init {
        initVertexData()
        initShader(context)
    }

    private fun initVertexData() {
        val vertices = floatArrayOf(
            -1f, 0f, 0.0f,
            1f, 0f, 0.0f,
            0.0f, 1f, 0.0f,
        )
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val colors = floatArrayOf(
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f
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

        MatrixState.setInitialState()
        // 绕Z轴旋转
        angle += 1f
        MatrixState.rotate(angle, 0f, 0f, 1f)

        GLES30.glUniformMatrix4fv(0, 1, false, MatrixState.getFinalMatrix(), 0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }

}