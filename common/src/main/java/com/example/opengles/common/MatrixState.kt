package com.example.opengles.common

import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 矩阵变换：最终矩阵mvpMatrix = projMatrix * viewMatrix * modelMatrix
 */
object MatrixState {
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val projMatrix = FloatArray(16)
    private var mvpMatrix = FloatArray(16)

    // 保存model matrix，以便进行各种变换
    private val modelMatrixStack = Array(10) { FloatArray(16) }
    private var stackTop = -1

    // 光源
    private val lightLocation = floatArrayOf(0f, 0f, 0f)
    private var lightPositionBuffer: FloatBuffer? = null
    private val lightTempBuffer = ByteBuffer.allocateDirect(3 * 4)

    private val lightDirection = floatArrayOf(0f, 0f, 1f)
    private var lightDirectionBuffer: FloatBuffer? = null
    private val lightDirectionTempBuffer = ByteBuffer.allocateDirect(3 * 4)

    private val cameraLocation = floatArrayOf(0f, 0f, 0f)
    private var cameraPositionBuffer: FloatBuffer? = null
    private val cameraTempBuffer = ByteBuffer.allocateDirect(3 * 4)

    @JvmStatic
    fun setLightLocation(x: Float, y: Float, z: Float) {
        lightLocation[0] = x
        lightLocation[1] = y
        lightLocation[2] = z

        lightTempBuffer.clear()
        lightTempBuffer.order(ByteOrder.nativeOrder())
        lightPositionBuffer = lightTempBuffer.asFloatBuffer()
        lightPositionBuffer?.put(lightLocation)
        lightPositionBuffer?.position(0)
    }

    @JvmStatic
    fun setLightDirection(x: Float, y: Float, z: Float) {
        lightDirection[0] = x
        lightDirection[1] = y
        lightDirection[2] = z

        lightDirectionTempBuffer.clear()
        lightDirectionTempBuffer.order(ByteOrder.nativeOrder())
        lightDirectionBuffer = lightDirectionTempBuffer.asFloatBuffer()
        lightDirectionBuffer?.put(lightDirection)
        lightDirectionBuffer?.position(0)
    }

    @JvmStatic
    fun getLightPositionBuffer(): FloatBuffer? {
        return lightPositionBuffer
    }

    @JvmStatic
    fun getCameraPositionBuffer(): FloatBuffer? {
        return cameraPositionBuffer
    }

    @JvmStatic
    fun getLightDirectionBuffer(): FloatBuffer? {
        return lightDirectionBuffer
    }

    @JvmStatic
    fun pushMatrix() {
        stackTop++
        for (i in 0..15) {
            modelMatrixStack[stackTop][i] = modelMatrix[i]
        }
    }

    @JvmStatic
    fun popMatrix() {
        for (i in 0..15) {
            modelMatrix[i] = modelMatrixStack[stackTop][i]
        }
        stackTop--
    }

    /**
     * @param cx 摄像机位置x
     * @param cy 摄像机位置y
     * @param cz 摄像机位置z
     * @param tx 摄像机目标点x
     * @param ty 摄像机目标点y
     * @param tz 摄像机目标点z
     * @param upx 摄像机UP向量X分量
     * @param upy 摄像机UP向量Y分量
     * @param upz 摄像机UP向量Z分量
     */
    @JvmStatic
    fun setCamera(
        cx: Float, cy: Float, cz: Float,
        tx: Float, ty: Float, tz: Float,
        upx: Float, upy: Float, upz: Float,
    ) {
        Matrix.setLookAtM(viewMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz)

        cameraLocation[0] = cx
        cameraLocation[1] = cy
        cameraLocation[2] = cz

        cameraTempBuffer.clear()
        cameraTempBuffer.order(ByteOrder.nativeOrder())
        cameraPositionBuffer = cameraTempBuffer.asFloatBuffer()
        cameraPositionBuffer?.put(cameraLocation)
        cameraPositionBuffer?.position(0)
    }

    /**
     * 设置透视投影参数
     *
     * @param left near面的left
     * @param right near面的right
     * @param bottom near面的bottom
     * @param top near面的top
     * @param near near面距离
     * @param far far面距离
     */
    @JvmStatic
    fun setProjectFrustum(
        left: Float,
        right: Float,
        bottom: Float,
        top: Float,
        near: Float,
        far: Float,
    ) {
        Matrix.frustumM(projMatrix, 0, left, right, bottom, top, near, far)
    }

    /**
     * 设置正交投影参数
     *
     * @param left near面的left
     * @param right near面的right
     * @param bottom near面的bottom
     * @param top near面的top
     * @param near near面的距离
     * @param far far面的距离
     */
    @JvmStatic
    fun setProjectOrtho(
        left: Float, right: Float,
        bottom: Float, top: Float,
        near: Float, far: Float
    ) {
        Matrix.orthoM(projMatrix, 0, left, right, bottom, top, near, far)
    }

    /**
     * 获取最终变换矩阵
     */
    @JvmStatic
    fun getFinalMatrix(): FloatArray {
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, mvpMatrix, 0)
        return mvpMatrix
    }

    @JvmStatic
    fun setInitialState() {
        Matrix.setIdentityM(modelMatrix, 0)
    }

    @JvmStatic
    fun rotate(a: Float, x: Float, y: Float, z: Float) {
        Matrix.rotateM(modelMatrix, 0, a, x, y, z)
    }

    @JvmStatic
    fun translate(x: Float, y: Float, z: Float) {
        Matrix.translateM(modelMatrix, 0, x, y, z)
    }

    @JvmStatic
    fun scale(x: Float, y: Float, z: Float) {
        Matrix.scaleM(modelMatrix, 0, x, y, z)
    }

    /**
     * 获取model matrix
     */
    @JvmStatic
    fun getModelMatrix(): FloatArray {
        return modelMatrix
    }
}