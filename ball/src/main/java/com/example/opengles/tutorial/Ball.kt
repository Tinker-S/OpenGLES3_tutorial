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
 * 球
 * <p/>
 * 绘制思路：按照经纬度两个维度将球面分割成若干三角形进行绘制
 * 每个三角形的顶点坐标：x = R×cosα×cosβ; y = R×cosα×sinβ; z = R×sinα
 */
class Ball(context: Context) {
    private var program: Int = 0
    private var angle = 0f
    private var vertexCount = 0

    private lateinit var vertexBuffer: FloatBuffer

    init {
        initVertexData()
        initShader(context)
    }

    private fun initVertexData() {
        // 球半径
        val r = 1f
        val verticeList = ArrayList<Float>()
        val angleSpan = 5
        var vAngle = -90
        while (vAngle < 90) {
            var hAngle = 0
            while (hAngle <= 360) {
                val x0 =
                    (r * cos(Math.toRadians(vAngle.toDouble())) * cos(Math.toRadians(hAngle.toDouble()))).toFloat()
                val y0 =
                    (r * cos(Math.toRadians(vAngle.toDouble())) * sin(Math.toRadians(hAngle.toDouble()))).toFloat()
                val z0 = (r * sin(Math.toRadians(vAngle.toDouble()))).toFloat()

                val x1 =
                    (r * cos(Math.toRadians(vAngle.toDouble())) * cos(Math.toRadians((hAngle + angleSpan).toDouble()))).toFloat()
                val y1 =
                    (r * cos(Math.toRadians(vAngle.toDouble())) * sin(Math.toRadians((hAngle + angleSpan).toDouble()))).toFloat()
                val z1 = (r * sin(Math.toRadians(vAngle.toDouble()))).toFloat()

                val x2 =
                    (r * cos(Math.toRadians((vAngle + angleSpan).toDouble())) * cos(Math.toRadians((hAngle + angleSpan).toDouble()))).toFloat()
                val y2 =
                    (r * cos(Math.toRadians((vAngle + angleSpan).toDouble())) * sin(Math.toRadians((hAngle + angleSpan).toDouble()))).toFloat()
                val z2 = (r * sin(Math.toRadians((vAngle + angleSpan).toDouble()))).toFloat()

                val x3 = (r * cos(Math.toRadians((vAngle + angleSpan).toDouble())) * cos(
                    Math.toRadians(hAngle.toDouble())
                )).toFloat()
                val y3 = (r * cos(Math.toRadians((vAngle + angleSpan).toDouble())) * sin(
                    Math.toRadians(hAngle.toDouble())
                )).toFloat()
                val z3 = (r * sin(Math.toRadians((vAngle + angleSpan).toDouble()))).toFloat()

                // 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
                verticeList.add(x1)
                verticeList.add(y1)
                verticeList.add(z1)
                verticeList.add(x3)
                verticeList.add(y3)
                verticeList.add(z3)
                verticeList.add(x0)
                verticeList.add(y0)
                verticeList.add(z0)
                verticeList.add(x1)
                verticeList.add(y1)
                verticeList.add(z1)
                verticeList.add(x2)
                verticeList.add(y2)
                verticeList.add(z2)
                verticeList.add(x3)
                verticeList.add(y3)
                verticeList.add(z3)

                hAngle += angleSpan
            }
            vAngle += angleSpan
        }

        // 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        vertexCount = verticeList.size / 3

        val vertices = verticeList.toFloatArray()
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
        angle += 1f
        MatrixState.rotate(angle, 0f, 1f, 0f)

        GLES30.glUniformMatrix4fv(0, 1, false, MatrixState.getFinalMatrix(), 0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

        GLES30.glDisableVertexAttribArray(0)
    }

}