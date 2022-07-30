package com.example.opengles.tutorial

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.example.opengles.tutorial.Normal.Companion.getAverage
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import kotlin.math.sqrt

object LoadUtil {
    //求两个向量的叉积
    private fun getCrossProduct(
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float
    ): FloatArray {
        //求出两个矢量叉积矢量在XYZ轴的分量ABC
        val A = y1 * z2 - y2 * z1
        val B = z1 * x2 - z2 * x1
        val C = x1 * y2 - x2 * y1
        return floatArrayOf(A, B, C)
    }

    //向量规格化
    fun vectorNormal(vector: FloatArray): FloatArray {
        //求向量的模
        val module =
            sqrt((vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]).toDouble()).toFloat()
        return floatArrayOf(vector[0] / module, vector[1] / module, vector[2] / module)
    }

    //从obj文件中加载携带顶点信息的物体，并自动计算每个顶点的平均法向量
    fun loadFromFile(fname: String?, r: Resources, context: Context?): LoadedObjectVertexNormal? {
        //加载后物体的引用
        var lo: LoadedObjectVertexNormal? = null
        //原始顶点坐标列表--直接从obj文件中加载
        val alv = ArrayList<Float>()
        //顶点组装面索引列表--根据面的信息从文件中加载
        val alFaceIndex = ArrayList<Int>()
        //结果顶点坐标列表--按面组织好
        val alvResult = ArrayList<Float>()
        //平均前各个索引对应的点的法向量集合Map
        //此HashMap的key为点的索引， value为点所在的各个面的法向量的集合
        val hmn = HashMap<Int, HashSet<Normal>>()
        try {
            val `in` = r.assets.open(fname!!)
            val isr = InputStreamReader(`in`)
            val br = BufferedReader(isr)
            var temps: String?

            //扫面文件，根据行类型的不同执行不同的处理逻辑
            while (br.readLine().also { temps = it } != null) {
                //用空格分割行中的各个组成部分
                val tempsa = temps!!.split("[ ]+".toRegex()).toTypedArray()
                if (tempsa[0].trim { it <= ' ' } == "v") { //此行为顶点坐标
                    //若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
                    alv.add(tempsa[1].toFloat())
                    alv.add(tempsa[2].toFloat())
                    alv.add(tempsa[3].toFloat())
                } else if (tempsa[0].trim { it <= ' ' } == "f") { //此行为三角形面
                    /*
                     *若为三角形面行则根据 组成面的顶点的索引从原始顶点坐标列表中
                     *提取相应的顶点坐标值添加到结果顶点坐标列表中，同时根据三个
                     *顶点的坐标计算出此面的法向量并添加到平均前各个索引对应的点
                     *的法向量集合组成的Map中
                     */
                    val index = IntArray(3) //三个顶点索引值的数组

                    //计算第0个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[0] = tempsa[1].split("/".toRegex()).toTypedArray()[0].toInt() - 1
                    val x0 = alv[3 * index[0]]
                    val y0 = alv[3 * index[0] + 1]
                    val z0 = alv[3 * index[0] + 2]
                    alvResult.add(x0)
                    alvResult.add(y0)
                    alvResult.add(z0)

                    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[1] = tempsa[2].split("/".toRegex()).toTypedArray()[0].toInt() - 1
                    val x1 = alv[3 * index[1]]
                    val y1 = alv[3 * index[1] + 1]
                    val z1 = alv[3 * index[1] + 2]
                    alvResult.add(x1)
                    alvResult.add(y1)
                    alvResult.add(z1)

                    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[2] = tempsa[3].split("/".toRegex()).toTypedArray()[0].toInt() - 1
                    val x2 = alv[3 * index[2]]
                    val y2 = alv[3 * index[2] + 1]
                    val z2 = alv[3 * index[2] + 2]
                    alvResult.add(x2)
                    alvResult.add(y2)
                    alvResult.add(z2)

                    //记录此面的顶点索引
                    alFaceIndex.add(index[0])
                    alFaceIndex.add(index[1])
                    alFaceIndex.add(index[2])

                    //通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
                    //求0号点到1号点的向量
                    val vxa = x1 - x0
                    val vya = y1 - y0
                    val vza = z1 - z0
                    //求0号点到2号点的向量
                    val vxb = x2 - x0
                    val vyb = y2 - y0
                    val vzb = z2 - z0
                    //通过求两个向量的叉积计算法向量
                    val vNormal = vectorNormal(
                        getCrossProduct(
                            vxa, vya, vza, vxb, vyb, vzb
                        )
                    )
                    for (tempInxex in index) { //记录每个索引点的法向量到平均前各个索引对应的点的法向量集合组成的Map中
                        //获取当前索引对应点的法向量集合
                        var hsn = hmn[tempInxex]
                        if (hsn == null) { //若集合不存在则创建
                            hsn = HashSet()
                        }
                        //将此点的法向量添加到集合中
                        //由于Normal类重写了equals方法，因此同样的法向量不会重复出现在此点
                        //对应的法向量集合中
                        hsn.add(Normal(vNormal[0], vNormal[1], vNormal[2]))
                        //将集合放进HsahMap中
                        hmn[tempInxex] = hsn
                    }
                }
            }

            //生成顶点数组
            val size = alvResult.size
            val vXYZ = FloatArray(size)
            for (i in 0 until size) {
                vXYZ[i] = alvResult[i]
            }

            //生成法向量数组
            val nXYZ = FloatArray(alFaceIndex.size * 3)
            var c = 0
            for (i in alFaceIndex) {
                //根据当前点的索引从Map中取出一个法向量的集合
                val hsn = hmn[i]!!
                //求出平均法向量
                val tn = getAverage(hsn)
                //将计算出的平均法向量存放到法向量数组中
                nXYZ[c++] = tn[0]
                nXYZ[c++] = tn[1]
                nXYZ[c++] = tn[2]
            }
            //创建3D物体对象
            lo = LoadedObjectVertexNormal(context!!, vXYZ, nXYZ)
        } catch (e: Exception) {
            Log.d("load error", "load error")
            e.printStackTrace()
        }
        return lo
    }
}