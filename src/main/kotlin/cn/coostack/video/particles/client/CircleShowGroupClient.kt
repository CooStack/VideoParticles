package cn.coostack.video.particles.client

import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffer
import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroupProvider
import cn.coostack.cooparticlesapi.particles.impl.TestEndRodEffect
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import cn.coostack.video.particles.client.sub.*
import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import org.joml.Vector3f
import java.util.*

class CircleShowGroupClient(uuid: UUID, val bindPlayer: UUID) : ControlableParticleGroup(uuid) {
    /**
     * 对应草稿前5步
     */
    private var status = 1

    class Provider : ControlableParticleGroupProvider {
        override fun changeGroup(group: ControlableParticleGroup, args: Map<String, ParticleControlerDataBuffer<*>>) {
            group as CircleShowGroupClient
            if (args.containsKey("status")) {
                group.status = args["status"]!!.loadedValue as Int
            }
            if (args.containsKey("flush")) {
                group.flush()
                group.axis = RelativeLocation(0.0, 1.0, 0.0)
            }
        }

        override fun createGroup(
            uuid: UUID,
            args: Map<String, ParticleControlerDataBuffer<*>>
        ): ControlableParticleGroup {
            val player = args["bind_player"]!!.loadedValue as UUID
            val group = CircleShowGroupClient(uuid, player)
            return group
        }
    }

    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        return when (status) {
            1 -> withStatus1()
            2 -> withStatus2()
            3 -> withStatus3()
            4 -> withStatus4()
            5 -> withStatus5()
            else -> hashMapOf()
        }
    }

    override fun onGroupDisplay() {
        addPreTickAction {
            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
            val seeDirection = player.rotationVector.normalize()
            val eye = player.eyePos.add(
                seeDirection.multiply(
                    if (status == 1) 1.0 else 8.0
                )
            )
            rotateParticlesToPoint(RelativeLocation.of(player.rotationVector))
            teleportTo(eye)
            if (status == 3) {
                rotateParticlesAsAxis(Math.toRadians(5.0))
            }
        }
    }

    private fun withStatus1(): Map<ParticleRelativeData, RelativeLocation> {
        return mutableMapOf(
            withEffect({ ParticleDisplayer.withSingle(TestEndRodEffect(it)) }) {
                color = Vector3f(100 / 255f, 100 / 255f, 255 / 255f)
            } to RelativeLocation()
        )
    }

    private fun withStatus2(): Map<ParticleRelativeData, RelativeLocation> {
        val locations = Math3DUtil.getCycloidGraphic(1.0, 1.0, 1, 1, 360, 2.0)
        val res = HashMap<ParticleRelativeData, RelativeLocation>()
        for (location in locations) {
            res[withEffect({
                ParticleDisplayer.withSingle(TestEndRodEffect(it))
            }) {
                color = Vector3f(100 / 255f, 100 / 255f, 255 / 255f)
            }] = location
        }
        return res
    }

    private fun withStatus3(): Map<ParticleRelativeData, RelativeLocation> {
        val locations = Math3DUtil.getCycloidGraphic(1.0, 1.0, 1, 1, 360, 2.0)
        val res = HashMap<ParticleRelativeData, RelativeLocation>()
        for (location in locations) {
            res[withEffect({
                ParticleDisplayer.withSingle(TestEndRodEffect(it))
            }) {
                color = Vector3f(100 / 255f, 100 / 255f, 255 / 255f)
            }] = location
        }
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicGroupClient(it).apply {
                    color = Vector3f(214 / 255f, 189 / 255f, 193 / 255f)
                    withDisplay = {
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(3.0))
                        }
                    }
                }
            )
        }) {}] = locations[0].clone()
        return res
    }

    private fun withStatus4(): Map<ParticleRelativeData, RelativeLocation> {

        return mutableMapOf(
            withEffect({
                ParticleDisplayer.withGroup(
                    MagicGroupClient(it).apply {
                        w1 = -2
                        w2 = 3
                        r1 = 3.0
                        r2 = 4.0
                        count = 360 * 2
                        color = Vector3f(100 / 255f, 100 / 255f, 255 / 255f)
                        withDisplay = {
                            addPreTickAction {
                                val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                                rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(3.0))
                            }
                        }
                    }
                )
            }) {} to RelativeLocation()
        )
    }

    private fun withStatus5(): Map<ParticleRelativeData, RelativeLocation> {
        val res = HashMap<ParticleRelativeData, RelativeLocation>()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicGroupClient(it).apply {
                    r1 = 3.0
                    r2 = 4.0
                    w1 = -2
                    w2 = 3
                    scale = 1.0
                    count = 1080
                    color = Vector3f(98 / 255f, 169 / 255f, 200 / 255f)
                    withDisplay = {
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(6.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicGroupClient(it).apply {
                    scale = 2.0
                    count = 360 * 2
                    color = Vector3f(168 / 255f, 209 / 255f, 225 / 255f)
                    withDisplay = {
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(3.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicGroupClient(it).apply {
                    scale = 3.0
                    count = 360 * 2
                    color = Vector3f(20 / 255f, 235 / 255f, 176 / 255f)
                    withDisplay = {
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(3.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicGroupClient(it).apply {
                    r1 = 4.0
                    r2 = 1.0
                    w1 = 1
                    w2 = 4
                    scale = 1.5
                    count = 720
                    color = Vector3f(243 / 255f, 204 / 255f, 219 / 255f)
                    withDisplay = {
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(-5.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicGroupClient(it).apply {
                    scale = 4.0
                    count = 360 * 3
                    color = Vector3f(168 / 255f, 209 / 255f, 225 / 255f)
                    withDisplay = {
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(6.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation()
        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicGroupClient(it).apply {
                    r1 = 1.0
                    r2 = 3.0
                    w1 = 5
                    w2 = 2
                    scale = 1.5
                    count = 1080
                    color = Vector3f(255 / 255f, 222 / 255f, 128 / 255f)
                    withDisplay = {
                        addPreTickAction {
                            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
                            rotateToWithAngle(RelativeLocation.of(player.rotationVector), Math.toRadians(9.0))
                        }
                    }
                }
            )
        }) {}] = RelativeLocation()
        return res
    }

}