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
 * 圆锥
 * <p />
 * 绘制思路：圆锥面由从顶点到园面的若干三角形构成，然后盖上一个圆就行了
 */
class Cone(context: Context) {
    private var program: Int = 0
    private var angle = 0f
    private var vertexCount = 0
    private var radius = 0.8f
    private var height = 2f

    private var circle: Circle
    private lateinit var vertexBuffer: FloatBuffer

    init {
        circle = Circle(context, radius)
        initVertexData()
        initShader(context)
    }

    private fun initVertexData() {
        val verticeList = mutableListOf<Float>()
        verticeList.add(0f)
        verticeList.add(0f)
        verticeList.add(-height)
        for (i in 0..360) {
            verticeList.add(radius * cos((i * Math.PI / 180f).toFloat()))
            verticeList.add(radius * sin((i * Math.PI / 180f).toFloat()))
            verticeList.add(0f)
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

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount)

        GLES30.glDisableVertexAttribArray(0)

        circle.drawSelf()
    }

}