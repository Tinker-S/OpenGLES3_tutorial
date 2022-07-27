package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import com.example.opengles.common.MatrixState
import com.example.opengles.common.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 立方体
 * <p/>
 * 绘制思路：立方体6个面，每个面由两个三角形组成，一共绘制12个三角形即可
 */
class Cube(context: Context) {
    private var program: Int = 0
    private var angle = 0f

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer
    private lateinit var indexBuffer: ByteBuffer

    // 顶点索引
    private var vertexIndexes: ByteArray? = null

    init {
        initVertexData()
        initShader(context)
    }

    private fun initVertexData() {
        val r = 1f
        // 立方体8个顶点
        val vertices = floatArrayOf(
            r, r, r,   // 0
            -r, r, r,  // 1
            -r, -r, r, // 2
            r, -r, r,  // 3
            r, r, -r,  // 4
            -r, r, -r, // 5
            -r, -r, -r,// 6
            r, -r, -r, // 7
        )

        // 每个三角形逆时针绘制
        val indexes = byteArrayOf(
            0, 1, 2, 0, 2, 3, //前面
            0, 4, 5, 0, 5, 1, //上面
            1, 5, 6, 1, 6, 2, //左面
            0, 3, 7, 0, 7, 4, //右面
            4, 6, 5, 4, 7, 6, //后面
            2, 6, 7, 2, 7, 3, //下面
        )
        vertexIndexes = indexes

        // 8个顶点，每个点对应一种颜色
        val colors = floatArrayOf(
            1f, 1f, 1f, 1f,
            0f, 1f, 1f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
        )

        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val cbb = ByteBuffer.allocateDirect(colors.size * 4)
        cbb.order(ByteOrder.nativeOrder())
        colorBuffer = cbb.asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)

        val ibb = ByteBuffer.allocateDirect(indexes.size)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb
        indexBuffer.put(indexes)
        indexBuffer.position(0)
    }

    private fun initShader(context: Context) {
        val vertexShader = ShaderUtil.loadFromAssetsFile("vertex.glsl", context.resources)
        val fragmentShader = ShaderUtil.loadFromAssetsFile("frag.glsl", context.resources)
        program = ShaderUtil.createProgram(vertexShader, fragmentShader)
    }

    fun drawSelf() {
        GLES30.glUseProgram(program)

        MatrixState.setInitialState()
        angle += 0.5f
        MatrixState.rotate(angle, 1f, 1f, 1f)

        GLES30.glUniformMatrix4fv(0, 1, false, MatrixState.getFinalMatrix(), 0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glEnableVertexAttribArray(1)

        vertexIndexes?.let {
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, it.size, GLES30.GL_UNSIGNED_BYTE, indexBuffer)
        }

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }

}