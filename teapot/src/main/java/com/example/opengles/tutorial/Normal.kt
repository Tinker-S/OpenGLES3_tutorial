package com.example.opengles.tutorial

import kotlin.math.abs

class Normal(var nx: Float, var ny: Float, var nz: Float) {
    override fun equals(other: Any?): Boolean {
        return if (other is Normal) {
            abs(nx - other.nx) < DIFF && abs(ny - other.ny) < DIFF && abs(ny - other.ny) < DIFF
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return 1
    }

    companion object {
        const val DIFF = 0.0000001f

        @JvmStatic
        fun getAverage(sn: Set<Normal>): FloatArray {
            //存放法向量和的数组
            val result = FloatArray(3)
            //把集合中所有的法向量求和
            for (n in sn) {
                result[0] += n.nx
                result[1] += n.ny
                result[2] += n.nz
            }
            //将求和后的法向量规格化
            return LoadUtil.vectorNormal(result)
        }
    }
}