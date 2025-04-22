package cn.coostack.video.particles.client

import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffer
import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroupProvider
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import cn.coostack.video.particles.client.sub.wand.MagicWandItemSubGroupClient
import org.joml.Vector3f
import java.util.*

class MagicWandGroupClient(uuid: UUID, val bindPlayer: UUID) : ControlableParticleGroup(uuid) {
    init {
        maxTick = 80
    }

    class Provider : ControlableParticleGroupProvider {
        override fun changeGroup(group: ControlableParticleGroup, args: Map<String, ParticleControlerDataBuffer<*>>) {
            group as MagicWandGroupClient
            if (args.containsKey("do_tick")) {
                group.doTick()
            }
            if (args.containsKey("over")) {
                group.over()
            }
        }

        override fun createGroup(
            uuid: UUID,
            args: Map<String, ParticleControlerDataBuffer<*>>
        ): ControlableParticleGroup {
            val group = MagicWandGroupClient(uuid, args["bind_player"]!!.loadedValue as UUID)
            group.tick = args["tick"]!!.loadedValue as Int
            group.toggleCT = args["toggle_ct"]!!.loadedValue as Int
            return group
        }
    }

    internal var toggleCT = 0

    fun over() {
        // 释放粒子 (barrier)
        clearParticles()
    }

    fun doTick() {
        // 生成粒子
        particlesLocations.forEach { t, u ->
            if (t.getControlObject() !is MagicWandItemSubGroupClient) {
                return@forEach
            }
            val control = t.getControlObject() as MagicWandItemSubGroupClient
            control.doTick()
            val sign = control.buffer["sign"] ?: return@forEach
            sign as Int
        }
    }

    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val res = HashMap<ParticleRelativeData, RelativeLocation>()
        val withBefore: MagicWandItemSubGroupClient.(Map<ParticleRelativeData, RelativeLocation>) -> Unit =
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
                MagicWandItemSubGroupClient(it).apply {
                    r1 = 3.0
                    r2 = 4.0
                    w1 = -2
                    w2 = 3
                    scale = 1.0
                    count = 1080
                    color = Vector3f(98 / 255f, 169 / 255f, 200 / 255f)
                    withDisplay = {
                        buffer["sign"] = 1
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(6.0))
                        }
                    }
                    withBeforeDisplay = withBefore
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicWandItemSubGroupClient(it).apply {
                    scale = 2.0
                    count = 360 * 2
                    color = Vector3f(168 / 255f, 209 / 255f, 225 / 255f)
                    withDisplay = {
                        buffer["sign"] = 2
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(3.0))
                        }
                    }
                    withBeforeDisplay = withBefore
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicWandItemSubGroupClient(it).apply {
                    scale = 3.0
                    count = 360 * 2
                    color = Vector3f(20 / 255f, 235 / 255f, 176 / 255f)
                    withDisplay = {
                        buffer["sign"] = 3
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(3.0))
                        }
                    }
                    withBeforeDisplay = withBefore
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicWandItemSubGroupClient(it).apply {
                    r1 = 4.0
                    r2 = 1.0
                    w1 = 1
                    w2 = 4
                    scale = 1.5
                    count = 720
                    color = Vector3f(243 / 255f, 204 / 255f, 219 / 255f)
                    withDisplay = {
                        buffer["sign"] = 4
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(-5.0))
                        }
                    }
                    withBeforeDisplay = withBefore
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicWandItemSubGroupClient(it).apply {
                    scale = 4.0
                    count = 360 * 3
                    color = Vector3f(168 / 255f, 209 / 255f, 225 / 255f)
                    withDisplay = {
                        buffer["sign"] = 5
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(6.0))
                        }
                    }
                    withBeforeDisplay = withBefore
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicWandItemSubGroupClient(it).apply {
                    r1 = 1.0
                    r2 = 3.0
                    w1 = 5
                    w2 = 2
                    scale = 1.5
                    count = 1080
                    color = Vector3f(255 / 255f, 222 / 255f, 128 / 255f)
                    withDisplay = {
                        buffer["sign"] = 6
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(9.0))
                        }
                    }
                    withBeforeDisplay = withBefore
                }
            )
        }) {}] = RelativeLocation()
        return res
    }

    override fun onGroupDisplay() {
        addPreTickAction {
            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
            val seeDirection = player.rotationVector.normalize()
            val eye = player.eyePos.add(
                seeDirection.multiply(8.0)
            )
            rotateParticlesToPoint(RelativeLocation.of(player.rotationVector))
            teleportTo(eye)
        }
        particlesLocations.forEach { t, u ->
            val controlObject = t.getControlObject()
            if (controlObject is MagicWandItemSubGroupClient) {
                controlObject.toggleTick(toggleCT)
            }
        }
    }
}