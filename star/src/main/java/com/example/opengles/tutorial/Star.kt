package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import com.example.opengles.common.MatrixState
import com.example.opengles.common.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 * 五角星
 */
class Star(
    context: Context,
    private val longRadius: Float = 0.5f,
    private val shortRadius: Float = 0.2f
) {
    private var program: Int = 0

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer

    private var vertexCount: Int = 0

    init {
        initVertexData()
        initShader(context)
    }

    private fun initVertexData() {
        // 计算五角星的顶点
        val count = 5
        val angleSpan = 360 / count
        // 10个三角形，一共30个顶点
        vertexCount = count * 2 * 3

        var currentAngle = 0
        val verticeList = mutableListOf<Float>()
        while (currentAngle < 360) {
            // 中心点
            verticeList.add(0f)
            verticeList.add(0f)
            // 凸点
            verticeList.add(longRadius * cos(Math.toRadians(currentAngle.toDouble())).toFloat())
            verticeList.add(longRadius * sin(Math.toRadians(currentAngle.toDouble())).toFloat())
            // 凹点
            verticeList.add(shortRadius * cos(Math.toRadians((currentAngle + angleSpan / 2).toDouble())).toFloat())
            verticeList.add(shortRadius * sin(Math.toRadians((currentAngle + angleSpan / 2).toDouble())).toFloat())

            // 中心点
            verticeList.add(0f)
            verticeList.add(0f)
            // 凹点
            verticeList.add(shortRadius * cos(Math.toRadians((currentAngle + angleSpan / 2).toDouble())).toFloat())
            verticeList.add(shortRadius * sin(Math.toRadians((currentAngle + angleSpan / 2).toDouble())).toFloat())
            // 凸点
            verticeList.add(longRadius * cos(Math.toRadians((currentAngle + angleSpan).toDouble())).toFloat())
            verticeList.add(longRadius * sin(Math.toRadians((currentAngle + angleSpan).toDouble())).toFloat())

            currentAngle += angleSpan
        }

        val vertices = verticeList.toFloatArray()
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val colorList = mutableListOf<Float>()
        for (i in verticeList.indices) {
            if (i % 3 == 0) {
                colorList.add(1f)
                colorList.add(1f)
                colorList.add(1f)
                colorList.add(1f)
            } else {
                colorList.add(1f)
                colorList.add(0f)
                colorList.add(0f)
                colorList.add(1f)
            }
        }
        val colors = colorList.toFloatArray()
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
        MatrixState.rotate(90f, 0f, 0f, 1f)
        MatrixState.translate(0f, 0f, 0.8f)

        GLES30.glUniformMatrix4fv(0, 1, false, MatrixState.getFinalMatrix(), 0)
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }

}