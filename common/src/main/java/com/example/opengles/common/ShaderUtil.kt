package com.example.opengles.common

import android.content.res.Resources
import android.opengl.GLES30
import android.util.Log
import java.io.ByteArrayOutputStream

object ShaderUtil {
    private const val TAG = "ShaderUtil"

    @JvmStatic
    fun loadShader(shaderType: Int, source: String?): Int {
        var shader = GLES30.glCreateShader(shaderType)
        if (shader != 0) {
            GLES30.glShaderSource(shader, source)
            GLES30.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader, shaderType: $shaderType")
                Log.e(TAG, GLES30.glGetShaderInfoLog(shader))
                GLES30.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    @JvmStatic
    fun createProgram(vertexSource: String?, fragmentSource: String?): Int {
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }
        val pixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
        if (pixelShader == 0) {
            return 0
        }
        var program = GLES30.glCreateProgram()
        if (program != 0) {
            GLES30.glAttachShader(program, vertexShader)
            checkGlError("glAttachShader")
            GLES30.glAttachShader(program, pixelShader)
            checkGlError("glAttachShader")
            GLES30.glLinkProgram(program)
            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES30.GL_TRUE) {
                Log.e(TAG, "Could not link program ${GLES30.glGetProgramInfoLog(program)}")
                GLES30.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    fun checkGlError(op: String) {
        var error: Int
        while (GLES30.glGetError().also { error = it } != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "$op: glError $error")
            throw RuntimeException("$op: glError $error")
        }
    }

    /**
     * 从sh脚本中加载shader内容的方法
     */
    @JvmStatic
    fun loadFromAssetsFile(fname: String?, r: Resources): String? {
        var result: String? = null
        try {
            val `in` = r.assets.open(fname!!)
            var ch: Int
            val baos = ByteArrayOutputStream()
            while (`in`.read().also { ch = it } != -1) {
                baos.write(ch)
            }
            val buff = baos.toByteArray()
            baos.close()
            `in`.close()
            result = String(buff)
            result = result.replace("\\r\\n".toRegex(), "\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
