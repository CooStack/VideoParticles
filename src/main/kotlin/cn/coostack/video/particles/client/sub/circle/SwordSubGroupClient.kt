package cn.coostack.video.particles.client.sub.circle

import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.impl.TestEndRodEffect
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import org.joml.Vector3f
import java.util.*

class SwordSubGroupClient(val targetPlayer: UUID, val color: Vector3f, uuid: UUID) : ControlableParticleGroup(uuid) {
    var hilt = 4
    var swordBody = 6
    var swordHeadLen = 3
    var bodySize = 1.0
    var tail = 4
    var step = 0.5

    init {
        axis = RelativeLocation.zAxis()
    }


    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        return genSword().associateBy {
            withEffect({ u ->
                ParticleDisplayer.withSingle(
                    TestEndRodEffect(u)
                )
            }) {
                color = this@SwordSubGroupClient.color
            }
        }
    }


    override fun beforeDisplay(locations: Map<ParticleRelativeData, RelativeLocation>) {
        val player = world!!.getPlayerByUuid(targetPlayer) ?: return
        val to = origin.relativize(player.pos)
        Math3DUtil.rotatePointsToPoint(locations.values.toList(), RelativeLocation.of(to), axis)
        axis = RelativeLocation.of(to)
    }

    override fun onGroupDisplay() {
        addPreTickAction {
            val player = world!!.getPlayerByUuid(targetPlayer) ?: return@addPreTickAction
            val to = origin.relativize(player.pos)
            rotateToWithAngle(RelativeLocation.of(to), Math.toRadians(10.0))
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
        for (i in -hilt..hilt) {
            res.add(RelativeLocation(i * step, 0.0, 0.0))
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