package cn.coostack.video.particles.client.sub.circle

import cn.coostack.cooparticlesapi.utils.RelativeLocation
import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import net.minecraft.particle.ParticleTypes
import org.joml.Vector3f
import java.util.UUID

class MagicPlayerViewSubGroup(val bindPlayer: UUID, uuid: UUID) : ControlableParticleGroup(uuid) {
    private var overTick = false
    override fun beforeDisplay(locations: Map<ParticleRelativeData, RelativeLocation>) {
        val player = world!!.getPlayerByUuid(bindPlayer) ?: return
        Math3DUtil.rotatePointsToPoint(
            locations.values.toList(),
            RelativeLocation.of(player.rotationVector),
            axis
        )
        axis = RelativeLocation.of(player.rotationVector)
    }

    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val res = mutableMapOf<ParticleRelativeData, RelativeLocation>()
        // player view
        val withBefore: MagicVarSizeSubGroup.(Map<ParticleRelativeData, RelativeLocation>) -> Unit =
            before@{ locs ->
                val player = world!!.getPlayerByUuid(bindPlayer) ?: return@before
                Math3DUtil.rotatePointsToPoint(
                    locs.values.toList(),
                    RelativeLocation.of(player.rotationVector),
                    axis
                )
                axis = RelativeLocation.of(player.rotationVector)
            }
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicVarSizeSubGroup(-2, 3, 3.0, 4.0, 1080, 1.0, it).apply {
                    beforeDisplayHandler = withBefore
                    onGroupDisplayHandler = {
                        addPreTickAction {
                            if (status == 2) return@addPreTickAction
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(6.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation(0.0, 6.0, 0.0)
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicVarSizeSubGroup(1, 1, 1.0, 1.0, 720, 3.0, it).apply {
                    color = Vector3f(168 / 255f, 209 / 255f, 225 / 255f)
                    beforeDisplayHandler = withBefore
                    onGroupDisplayHandler = {
                        addPreTickAction {
                            if (status == 2) return@addPreTickAction
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(6.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation(0.0, 6.0, 0.0)
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicVarSizeSubGroup(1, 1, 1.0, 1.0, 720, 5.0, it).apply {
                    color = Vector3f(20 / 255f, 235 / 255f, 176 / 255f)
                    beforeDisplayHandler = withBefore
                    onGroupDisplayHandler = {
                        addPreTickAction {
                            if (status == 2) return@addPreTickAction
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(3.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation(0.0, 10.0, 0.0)
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicVarSizeSubGroup(1, 4, 4.0, 1.0, 720, 1.5, it).apply {
                    color = Vector3f(243 / 255f, 204 / 255f, 219 / 255f)
                    onGroupDisplayHandler = {
                        addPreTickAction {
                            if (status == 2) return@addPreTickAction
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(-5.0))
                        }
                    }
                    beforeDisplayHandler = withBefore
                }
            )
        }) {}] = RelativeLocation(0.0, 12.0, 0.0)
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicVarSizeSubGroup(1, 1, 1.0, 1.0, 720, 4.0, it).apply {
                    color = Vector3f(168 / 255f, 209 / 255f, 225 / 255f)
                    onGroupDisplayHandler = {
                        addPreTickAction {
                            if (status == 2) return@addPreTickAction
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(6.0))
                        }
                    }
                    beforeDisplayHandler = withBefore
                }
            )
        }) {}] = RelativeLocation(0.0, 14.0, 0.0)
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicVarSizeSubGroup(5, 2, 1.0, 3.0, 1080, 1.5, it).apply {
                    color = Vector3f(255 / 255f, 222 / 255f, 128 / 255f)
                    onGroupDisplayHandler = {
                        addPreTickAction {
                            if (status == 2) return@addPreTickAction
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(9.0))
                        }
                    }
                    beforeDisplayHandler = withBefore
                }
            )
        }) {}] = RelativeLocation(0.0, 16.0, 0.0)
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicVarSizeSubGroup(1, 1, 1.0, 1.0, 720, 5.0, it).apply {
                    color = Vector3f(168 / 255f, 209 / 255f, 225 / 255f)
                    onGroupDisplayHandler = {
                        addPreTickAction {
                            if (status == 2) return@addPreTickAction
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(6.0))
                        }
                    }
                    beforeDisplayHandler = withBefore
                }
            )
        }) {}] = RelativeLocation(0.0, 16.0, 0.0)
        return res
    }

    fun over() {
        if (overTick) {
            return
        }
        particlesLocations.forEach { t, u ->
            val obj = t.getControlObject()
            if (obj is MagicVarSizeSubGroup) {
                obj.changeStatus(2, 60)
            }
        }
        overTick = true
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
        val rounds = Math3DUtil.getRoundScapeLocations(7.0, 1.0, 36, 360)
        addPreTickAction {
            if (overTick) {
                return@addPreTickAction
            }
            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
            rotateParticlesToPoint(
                RelativeLocation.of(
                    player.rotationVector
                )
            )
        }

        addPreTickAction {
            if (overTick) {
                return@addPreTickAction
            }
            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
            val clone = rounds.random()
            Math3DUtil.rotatePointsToPoint(
                listOf(clone),
                RelativeLocation.of(player.rotationVector),
                RelativeLocation.yAxis()
            )
            val to = player.eyePos.add(clone.toVector())
            val dir = player.rotationVector.normalize().multiply(2.0)
            world!!.addParticle(
                ParticleTypes.END_ROD, to.x, to.y, to.z,
                dir.x, dir.y, dir.z
            )
        }
    }
}