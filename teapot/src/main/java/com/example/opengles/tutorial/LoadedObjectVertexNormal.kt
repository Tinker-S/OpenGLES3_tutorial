package com.example.opengles.tutorial

import android.content.Context
import android.opengl.GLES30
import com.example.opengles.common.MatrixState.getCameraPositionBuffer
import com.example.opengles.common.MatrixState.getFinalMatrix
import com.example.opengles.common.MatrixState.getLightPositionBuffer
import com.example.opengles.common.MatrixState.getModelMatrix
import com.example.opengles.common.ShaderUtil.createProgram
import com.example.opengles.common.ShaderUtil.loadFromAssetsFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class LoadedObjectVertexNormal(context: Context, vertices: FloatArray, normals: FloatArray) {
    var mProgram = 0
    var muMVPMatrixHandle = 0
    var muMMatrixHandle = 0
    var maPositionHandle = 0
    var maNormalHandle = 0
    var maLightLocationHandle = 0
    var maCameraHandle = 0
    var mVertexBuffer: FloatBuffer? = null
    var mNormalBuffer: FloatBuffer? = null
    var vCount = 0

    private fun initVertexData(vertices: FloatArray, normals: FloatArray) {
        vCount = vertices.size / 3

        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        mVertexBuffer = vbb.asFloatBuffer()
        mVertexBuffer?.put(vertices)
        mVertexBuffer?.position(0)

        val cbb = ByteBuffer.allocateDirect(normals.size * 4)
        cbb.order(ByteOrder.nativeOrder())
        mNormalBuffer = cbb.asFloatBuffer()
        mNormalBuffer?.put(normals)
        mNormalBuffer?.position(0)
    }

    private fun initShader(context: Context) {
        val vertexShader = loadFromAssetsFile("vertex.glsl", context.resources)
        val fragmentShader = loadFromAssetsFile("frag.glsl", context.resources)
        mProgram = createProgram(vertexShader, fragmentShader)
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition")
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal")
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix")
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation")
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera")
    }

    fun drawSelf() {
        GLES30.glUseProgram(mProgram)
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFinalMatrix(), 0)
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, getModelMatrix(), 0)
        GLES30.glUniform3fv(maLightLocationHandle, 1, getLightPositionBuffer())
        GLES30.glUniform3fv(maCameraHandle, 1, getCameraPositionBuffer())
        GLES30.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        GLES30.glVertexAttribPointer(
            maNormalHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            mNormalBuffer
        )
        GLES30.glEnableVertexAttribArray(maPositionHandle)
        GLES30.glEnableVertexAttribArray(maNormalHandle)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount)
        GLES30.glDisableVertexAttribArray(maPositionHandle)
        GLES30.glDisableVertexAttribArray(maNormalHandle)
    }

    init {
        initVertexData(vertices, normals)
        initShader(context)
    }
}