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
 * 圆形（多边形）
 */
class Circle(context: Context) {
    private var program: Int = 0

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer

    // 圆形顶点数据
    private lateinit var vertexData: FloatArray
    // 圆形颜色数据
    private lateinit var colorData: FloatArray

    init {
        initVertexData()
        initShader(context)
    }

    private fun initVertexData() {
        createCircleData(0.5f, 60)

        val vbb = ByteBuffer.allocateDirect(vertexData.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertexData)
        vertexBuffer.position(0)

        val cbb = ByteBuffer.allocateDirect(colorData.size * 4)
        cbb.order(ByteOrder.nativeOrder())
        colorBuffer = cbb.asFloatBuffer()
        colorBuffer.put(colorData)
        colorBuffer.position(0)
    }

    /**
     * count为3就是三角形，为4就是四边行，为N是N变形，设置比较大例如60视觉上就是圆形了
     */
    private fun createCircleData(radius: Float, count: Int) {
        val data = mutableListOf(0f, 0f, 0f)
        val span = 360f / count
        var i = 0f
        while (i < 360 + span) {
            data.add(radius * sin(i * Math.PI / 180f).toFloat())
            data.add(radius * cos(i * Math.PI / 180f).toFloat())
            data.add(0f)
            i += span
        }
        vertexData = data.toFloatArray()

        val tempC = mutableListOf<Float>()
        val totalC = mutableListOf<Float>()
        tempC.add(1f)
        tempC.add(0f)
        tempC.add(0f)
        tempC.add(1f)
        for (k in 0..vertexData.size / 3) {
            totalC.addAll(tempC)
        }
        colorData = totalC.toFloatArray()
    }

    private fun initShader(context: Context) {
        val vertexShader = ShaderUtil.loadFromAssetsFile("vertex.glsl", context.resources)
        val fragmentShader = ShaderUtil.loadFromAssetsFile("frag.glsl", context.resources)
        program = ShaderUtil.createProgram(vertexShader, fragmentShader)
    }

    fun drawSelf() {
        GLES30.glUseProgram(program)

        GLES30.glUniformMatrix4fv(0, 1, false, MatrixState.getFinalMatrix(), 0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexData.size / 3)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)
    }

}