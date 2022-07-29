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
 * 圆柱体
 * <p />
 * 绘制思路：圆柱面分割成若干个矩形，使用GL_TRIANGLE_STRIP方式连接，然后上下分别绘制两个圆盖上就行了
 */
class Cylinder(context: Context) {
    private var program: Int = 0
    private var angle = 0f
    private var vertexCount = 0
    private var radius = 0.8f

    private var upCircle: Circle
    private var downCircle: Circle
    private lateinit var vertexBuffer: FloatBuffer

    init {
        upCircle = Circle(context, radius, 1f)
        downCircle = Circle(context, radius, -1f)
        initVertexData()
        initShader(context)
    }

    private fun initVertexData() {
        val verticeList = mutableListOf<Float>()
        val step = 1
        for (i in 0..360) {
            verticeList.add(radius * cos((i * Math.PI / 180f).toFloat()))
            verticeList.add(1f)
            verticeList.add(radius * sin((i * Math.PI / 180f).toFloat()))

            verticeList.add(radius * cos((i * Math.PI / 180f).toFloat()))
            verticeList.add(-1f)
            verticeList.add(radius * sin((i * Math.PI / 180f).toFloat()))

            verticeList.add(radius * cos(((i + step) * Math.PI / 180f).toFloat()))
            verticeList.add(1f)
            verticeList.add(radius * sin(((i + step) * Math.PI / 180f).toFloat()))

            verticeList.add(radius * cos(((i + step) * Math.PI / 180f).toFloat()))
            verticeList.add(-1f)
            verticeList.add(radius * sin(((i + step) * Math.PI / 180f).toFloat()))
        }

        val vertices = verticeList.toFloatArray()
        vertexCount = vertices.size / 3
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)
    }

    private fun initShader(context: Context) {
        val vertexShader = ShaderUtil.loadFromAssetsFile("vertex.glsl", context.resources)
        val fragmentShader = ShaderUtil.loadFromAssetsFile("frag.glsl", context.resources)
        program = ShaderUtil.createProgram(vertexShader, fragmentShader)
    }

    fun drawSelf() {
        GLES30.glUseProgram(program)

        MatrixState.setInitialState()
        MatrixState.rotate(-60f, 1f, 0f, 0f)
        angle += 1f
        MatrixState.rotate(angle, 1f, 1f, 1f)

        GLES30.glUniformMatrix4fv(0, 1, false, MatrixState.getFinalMatrix(), 0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vertexCount)

        GLES30.glDisableVertexAttribArray(0)

        upCircle.drawSelf()
        downCircle.drawSelf()
    }

}
