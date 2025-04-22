package cn.coostack.video.util

import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import java.util.ArrayList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

object VMathUtil {
    fun getBallLocations(r: Double, countPow: Int): MutableList<RelativeLocation> {
        val result = ArrayList<RelativeLocation>()
        var rx = 0.0
        var ry = 0.0
        val step = 2 * PI / countPow
        for (i in 1..countPow) {
            for (j in 1..countPow) {
                // 将PI 分割成 countPow份
                result.add(
                    RelativeLocation(
                        r * cos(rx) * cos(ry),
                        r * sin(rx) * sin(ry),
                        r * sin(ry)
                    )
                )
                ry += step
            }
            rx += step
        }
        return result
    }

    /**
     * 获取圆面
     * 圆面在XZ上
     * @param r 圆的半径
     * @param step 圆环之间的间距
     * @param preCircleCount 每个圆环的粒子个数
     */
    fun getRoundScapeLocations(r: Double, step: Double, preCircleCount: Int): MutableList<RelativeLocation> {
        val res = mutableListOf<RelativeLocation>()
        if (step <= 0 || r < step) {
            return res
        }
        var varR = step
        while (varR < r) {
            val stepCircle = 2 * PI / preCircleCount
            for (i in 1..preCircleCount) {
                val x = varR * cos(stepCircle * i)
                val z = varR * sin(stepCircle * i)
                res.add(
                    RelativeLocation(x, 0.0, z)
                )
            }
            varR += step
        }

        return res
    }

    /**
     * 获取圆面
     * 圆面在XZ上
     * @param r 圆的半径
     * @param step 圆环之间的间距
     * @param minCircleCount 一个圆环粒子个数的最小值
     * @param maxCircleCount 一个圆环粒子个数的最大值
     */
    fun getRoundScapeLocations(
        r: Double,
        step: Double,
        minCircleCount: Int,
        maxCircleCount: Int
    ): MutableList<RelativeLocation> {
        val res = mutableListOf<RelativeLocation>()
        if (step <= 0 || r < step) {
            return res
        }
        // 一共拥有圆环的个数
        val circleTotalCount = (r / step).toInt()
        var varR = step
        // 当前圆环的编号
        var currentCircle = 1
        // 小圆环到大圆环之间 粒子的差异
        val countStep = (maxCircleCount - minCircleCount) / circleTotalCount
        while (varR < r) {
            val currentCircleParticleCount =
                minCircleCount + currentCircle * countStep
            val stepCircle = 2 * PI / currentCircleParticleCount
            for (i in 1..currentCircleParticleCount) {
                val x = varR * cos(stepCircle * i)
                val z = varR * sin(stepCircle * i)
                res.add(
                    RelativeLocation(x, 0.0, z)
                )
            }
            varR += step
            currentCircle++
        }
        return res
    }

    /**
     * @param height 圆柱的高
     * @param heightStep 圆柱面之间的间距
     * @param r 圆柱的底面积半径
     * @param step 圆柱底面积圆环之间的间距
     * @param preCircleCount 圆柱底面积圆环的粒子个数
     */
    fun getCylinderLocations(
        height: Double,
        heightStep: Double,
        r: Double,
        step: Double,
        preCircleCount: Int
    ): MutableList<RelativeLocation> {
        if (height < heightStep) {
            return mutableListOf()
        }
        val start = getRoundScapeLocations(r, step, preCircleCount)
        val end = getRoundScapeLocations(r, step, preCircleCount).onEach {
            it.y += height
        }
        val heightCount = (height / heightStep).toInt()
        val res = mutableListOf<RelativeLocation>()
        for ((index, startLoc) in start.withIndex()) {
            val endLoc = end[index]
            res.addAll(
                Math3DUtil.getLineLocations(
                    startLoc.toVector(), endLoc.toVector(), heightStep, heightCount
                )
            )
        }
        return res
    }

    /**
     * @param height 圆柱的高
     * @param heightStep 圆柱面之间的间距
     * @param r 圆柱的底面积半径
     * @param step 圆柱底面积圆环之间的间距
     * @param preCircleCount 圆柱底面积圆环的粒子个数
     */
    fun getCylinderLocations(
        height: Double,
        heightStep: Double,
        r: Double,
        step: Double,
        minCircleCount: Int,
        maxCircleCount: Int
    ): MutableList<RelativeLocation> {
        if (height < heightStep) {
            return mutableListOf()
        }
        val start = getRoundScapeLocations(r, step, minCircleCount, maxCircleCount)
        val end = getRoundScapeLocations(r, step, minCircleCount, maxCircleCount).onEach {
            it.y += height
        }
        val heightCount = (height / heightStep).toInt()
        val res = mutableListOf<RelativeLocation>()
        for ((index, startLoc) in start.withIndex()) {
            val endLoc = end[index]
            res.addAll(
                Math3DUtil.getLineLocations(
                    startLoc.toVector(), endLoc.toVector(), heightCount
                )
            )
        }
        return res
    }

}