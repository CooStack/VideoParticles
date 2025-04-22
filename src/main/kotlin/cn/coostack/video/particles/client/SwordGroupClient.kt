package cn.coostack.video.particles.client

import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroupProvider
import cn.coostack.cooparticlesapi.particles.impl.TestEndRodEffect
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import net.minecraft.util.math.Vec3d
import java.util.UUID
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.roundToInt

class SwordGroupClient(uuid: UUID) : ControlableParticleGroup(uuid) {
    var hilt = 2
    var swordBody = 6
    var swordHeadLen = 3
    var bodySize = 1.0
    var tail = 4
    var step = 0.25
    var direction: RelativeLocation = RelativeLocation()
    var rotateStart = 0.0
    var rotateDirection = -1

    init {
        axis = RelativeLocation.zAxis()
    }

    class Provider : ControlableParticleGroupProvider {
        override fun createGroup(
            uuid: UUID,
            args: Map<String, ParticleControlerDataBuffer<*>>
        ): ControlableParticleGroup {
            val group = SwordGroupClient(uuid)
            group.direction = RelativeLocation.of(args["direction"]!!.loadedValue as Vec3d)
            group.rotateStart = args["rotateStart"]!!.loadedValue as Double
            group.rotateDirection = args["rotateDirection"]!!.loadedValue as Int
            return group
        }

        override fun changeGroup(
            group: ControlableParticleGroup,
            args: Map<String, ParticleControlerDataBuffer<*>>
        ) {
            group as SwordGroupClient
            if (args.containsKey("direction")) {
                group.direction = RelativeLocation.of(args["direction"]!!.loadedValue as Vec3d)
            }
            if (args.containsKey("rotateStart")) {
                group.rotateStart = args["rotateStart"]!!.loadedValue as Double
            }
            if (args.containsKey("rotateDirection")) {
                group.rotateDirection = args["rotateDirection"]!!.loadedValue as Int
            }
        }
    }


    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val res = mutableMapOf<ParticleRelativeData, RelativeLocation>()
        genSword().forEach {
            res[withEffect({ uuid ->
                ParticleDisplayer.withSingle(
                    TestEndRodEffect(uuid)
                )
            }) {}] = it
        }
        return res
    }

    override fun beforeDisplay(locations: Map<ParticleRelativeData, RelativeLocation>) {
        Math3DUtil.rotatePointsToPoint(locations.values.toList(), direction, axis)
        axis = direction
        Math3DUtil.rotateAsAxis(locations.values.toList(), axis, rotateStart)
    }

    override fun onGroupDisplay() {
        addPreTickAction {
            rotateParticlesAsAxis(PI / 72.0 * rotateDirection)
        }
        addPreTickAction {
            if (direction.length() !in -1e-4..1e-4) {
                return@addPreTickAction
            }
            if (axis == direction) {
                return@addPreTickAction
            }
            rotateParticlesToPoint(direction)
        }
    }

    /**   z
     *   / \ -> h = swordHeadLen 是sword body最上方开始记录
     *   | | -> sword body
     *   |a| a-> bodySize -> -bodySize , bodySize
     *  ----- -> sword hilt * 2 + 1   - >> x
     *    | --> sword tail
     */
    private fun genSword(): List<RelativeLocation> {
        val res = mutableListOf<RelativeLocation>()
        var c = -hilt + 0.0
        while (c < hilt) {
            res.add(RelativeLocation(c, 0.0, 0.0))
            c += step
        }

        for (i in 1..swordBody) {
            res.add(
                RelativeLocation(
                    -bodySize * step, 0.0, step * i
                )
            )
            res.add(
                RelativeLocation(
                    bodySize * step, 0.0, step * i
                )
            )
        }
        for (i in 1..tail) {
            res.add(
                RelativeLocation(
                    0.0, 0.0, -step * i
                )
            )
        }

        val highest = RelativeLocation(0.0, 0.0, step * (swordBody + swordHeadLen))
        val l = RelativeLocation(-bodySize * step, 0.0, step * swordBody)
        val r = RelativeLocation(+bodySize * step, 0.0, step * swordBody)
        res.addAll(
            Math3DUtil.getLineLocations(
                highest.toVector(), l.toVector(), swordBody
            )
        )
        res.addAll(
            Math3DUtil.getLineLocations(
                highest.toVector(), r.toVector(), swordBody
            )
        )
        return res
    }
}