package cn.coostack.video.particles.client.sub.circle

import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import net.minecraft.particle.ParticleTypes
import org.joml.Vector3f
import java.util.UUID
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MagicExtendSubGroup(val bindPlayer: UUID, uuid: UUID) : ControlableParticleGroup(uuid) {
    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val res = mutableMapOf<ParticleRelativeData, RelativeLocation>()
        val locs = mutableListOf<RelativeLocation>()

//        locs.addAll(Math3DUtil.getBallLocations(14.0, 3))
//        locs.onEach {
//            it.y += 1.0
//        }
        for (i in 1..4) {
            val l = withCircleLocation(
                13.0,
                PI * i / 2,
                14.0
            )
            locs.add(l)
        }
        for (i in 1..4) {
            val l = withCircleLocation(
                -11.0,
                PI * i / 2,
                14.0
            )
            locs.add(l)
        }
        locs.forEach { l ->
            res[withEffect({
                ParticleDisplayer.withGroup(
                    MagicVarSizeSubGroup(
                        -3, 2, 2.0, 3.0, 360, 0.5, it
                    ).also { g ->
                        g.color = Vector3f(
                            247 / 255f, 120 / 255f, 243 / 255f
                        )
                        g.beforeDisplayHandler = action@{
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@action
                            val to = player.pos.relativize(origin)
                            Math3DUtil.rotatePointsToPoint(
                                it.values.toList(),
                                RelativeLocation.of(to),
                                RelativeLocation.yAxis()
                            )
                            axis = RelativeLocation.of(to)
                        }
                        g.onGroupDisplayHandler = {
                            addPreTickAction {
                                val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                                val to = player.pos.relativize(origin)
                                rotateParticlesToPoint(RelativeLocation.of(to))
                                rotateParticlesAsAxis(PI / 72)
                            }
                        }
                    }
                )
            }) {}] = l
            res[withEffect({
                ParticleDisplayer.withGroup(
                    MagicVarSizeSubGroup(
                        1, 1, 1.0, 1.0, 360, 1.5, it
                    ).also { g ->
                        g.color = Vector3f(
                            247 / 255f, 120 / 255f, 243 / 255f
                        )
                        g.beforeDisplayHandler = action@{
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@action
                            val to = player.pos.relativize(origin)
                            Math3DUtil.rotatePointsToPoint(
                                it.values.toList(),
                                RelativeLocation.of(to),
                                RelativeLocation.yAxis()
                            )
                            axis = RelativeLocation.of(to)
                        }
                        g.onGroupDisplayHandler = {
                            addPreTickAction {
                                val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                                val to = player.pos.relativize(origin)
                                rotateParticlesToPoint(RelativeLocation.of(to))
                                rotateParticlesAsAxis(-PI / 72)
                            }
                        }
                    }
                )
            }) {}] = l
        }
        return res
    }

    fun onChange(status: Int?, duration: Int?, currentTick: Int?) {
        particlesLocations.forEach { t, u ->
            val controlObject = t.getControlObject()
            if (controlObject !is MagicVarSizeSubGroup) {
                return@forEach
            }
            controlObject.onTopGroupChange(status, duration, currentTick)
        }
    }

    override fun onGroupDisplay() {
        addPreTickAction {
            rotateParticlesAsAxis(PI / 72)
            particlesLocations.values.forEach { location ->
                val toLoc = origin.add(location.toVector())
                val negative = location.normalize().multiply(-1.5)
                world!!.addParticle(
                    ParticleTypes.END_ROD, true, toLoc.x, toLoc.y, toLoc.z,
                    negative.x, negative.y, negative.z
                )
            }
        }
    }

    fun over() {
        particlesLocations.forEach { t, u ->
            val obj = t.getControlObject()
            if (obj is MagicVarSizeSubGroup) {
                obj.changeStatus(2, 60)
            }
        }
    }

    private fun withCircleLocation(y: Double, rad: Double, radius: Double): RelativeLocation {
        return RelativeLocation(
            radius * cos(rad),
            y,
            radius * sin(rad),
        )
    }

}