package cn.coostack.video.particles.client

import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffer
import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroupProvider
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import cn.coostack.video.items.VideoItems
import cn.coostack.video.particles.client.sub.circle.*
import org.joml.Vector3f
import java.util.*
import kotlin.math.PI

class MagicCircleGroupClient(uuid: UUID, val bindPlayer: UUID) : ControlableParticleGroup(uuid) {
    class Provider : ControlableParticleGroupProvider {
        override fun changeGroup(group: ControlableParticleGroup, args: Map<String, ParticleControlerDataBuffer<*>>) {
            group as MagicCircleGroupClient
            toggle(group, args)
            group.onChange(
                args.containsKey("status") && args.containsKey("duration"),
                args.containsKey("currentTick")
            )
        }

        override fun createGroup(
            uuid: UUID,
            args: Map<String, ParticleControlerDataBuffer<*>>
        ): ControlableParticleGroup {
            val group = MagicCircleGroupClient(uuid, args["bind_player"]!!.loadedValue as UUID)
            // 同步status
            toggle(group, args)
            return group
        }

        private fun toggle(group: MagicCircleGroupClient, args: Map<String, ParticleControlerDataBuffer<*>>) {
            if (args.containsKey("status")) {
                group.currentStatus = args["status"]!!.loadedValue as Int
            }
            if (args.containsKey("duration")) {
                group.currentDuration = args["duration"]!!.loadedValue as Int
            }

            if (args.containsKey("currentTick")) {
                group.currentStatusTick = args["currentTick"]!!.loadedValue as Int
                if (group.currentStatus == 2) {
                    group.current = group.currentStatusTick
                }
            }
            if (args.containsKey("over")) {
                group.over()
            }
        }
    }

    private var overTick = false
    private var maxDuration = 60
    private var current = 0

    // 同步项目
    var currentStatus = 0
    var currentStatusTick = 0
    var currentDuration = 60


    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val res = mutableMapOf<ParticleRelativeData, RelativeLocation>()
        res[
            withEffect(
                { ParticleDisplayer.withGroup(MagicBottomSubGroup(it)) }
            ) {}
        ] = RelativeLocation(0.0, 0.1, 0.0)

        res[
            withEffect({
                ParticleDisplayer.withGroup(
                    MagicVarSizeSubGroup(1, 1, 1.0, 1.0, 360, 5.0, it)
                        .also { g ->
                            g.color = Vector3f(232 / 255f, 247 / 255f, 120 / 255f)
                            g.changeStatus(currentStatus, currentDuration)
                            g.currentTick = currentStatusTick
                            g.onGroupDisplayHandler = action@{
                                addPreTickAction {
                                    if (overTick) {
                                        return@addPreTickAction
                                    }
                                    rotateParticlesAsAxis(PI / 72)
                                }
                            }
                        }
                )
            }) {}
        ] = RelativeLocation(
            0.0, 2.0, 0.0
        )

        res[
            withEffect({
                ParticleDisplayer.withGroup(
                    MagicVarSizeSubGroup(1, 1, 1.0, 1.0, 360, 2.0, it)
                        .also { g ->
                            g.color = Vector3f(232 / 255f, 247 / 255f, 120 / 255f)
                            g.changeStatus(currentStatus, currentDuration)
                            g.currentTick = currentStatusTick
                            g.onGroupDisplayHandler = action@{
                                addPreTickAction {
                                    if (overTick) {
                                        return@addPreTickAction
                                    }
                                    rotateParticlesAsAxis(-PI / 72)
                                }
                            }
                        }
                )
            }) {}
        ] = RelativeLocation(
            0.0, 3.5, 0.0
        )

        res[
            withEffect(
                { ParticleDisplayer.withGroup(MagicBottomSubGroup(it)) }
            ) {}
        ] = RelativeLocation(0.0, 5.5, 0.0)

        res[withEffect(
            { ParticleDisplayer.withGroup(MagicExtendSubGroup(bindPlayer, it)) }
        ) {}] = RelativeLocation(0.0, 1.0, 0.0)

        res[withEffect({
            ParticleDisplayer.withGroup(
                MagicPlayerViewSubGroup(bindPlayer, it)
            )
        }
        ) {}] = RelativeLocation(0.0, 2.0, 0.0)
        return res
    }

    override fun beforeDisplay(locations: Map<ParticleRelativeData, RelativeLocation>) {
    }

    fun over() {
        if (overTick) {
            return
        }
        currentStatus = 2
        maxDuration = 60
        currentStatusTick = 0
        particlesLocations.forEach { t, u ->
            val obj = t.getControlObject()
            if (obj is MagicBottomSubGroup) {
                obj.over()
            }
            if (obj is MagicExtendSubGroup) {
                obj.over()
            }

            if (obj is MagicPlayerViewSubGroup) {
                obj.over()
            }

            if (obj is MagicVarSizeSubGroup) {
                obj.changeStatus(currentStatus, currentDuration)
            }
        }
        overTick = true
    }

    override fun onGroupDisplay() {
        addPreTickAction {
            if (overTick) {
                if (current++ > maxDuration) {
                    clearParticles()
                }
                return@addPreTickAction
            }
            val player = world!!.getPlayerByUuid(bindPlayer) ?: return@addPreTickAction
            if (!player.activeItem.isOf(VideoItems.redWandItem) && !player.activeItem.isOf(VideoItems.purpleWandItem)) {
                over()
            }
            teleportTo(player.pos)
        }
    }

    fun onChange(changeStatus: Boolean, changeTick: Boolean) {
        particlesLocations.forEach { t, u ->
            val obj = t.getControlObject()
            if (obj is MagicBottomSubGroup) {
                obj.onChange(
                    if (changeStatus) currentStatus else null,
                    if (changeStatus) currentDuration else null,
                    if (changeTick) currentStatusTick else null
                )
            }
            if (obj is MagicExtendSubGroup) {
                obj.onChange(
                    if (changeStatus) currentStatus else null,
                    if (changeStatus) currentDuration else null,
                    if (changeTick) currentStatusTick else null
                )
            }

            if (obj is MagicPlayerViewSubGroup) {
                obj.onChange(
                    if (changeStatus) currentStatus else null,
                    if (changeStatus) currentDuration else null,
                    if (changeTick) currentStatusTick else null
                )
            }

            if (obj is MagicVarSizeSubGroup) {
                if (changeStatus) {
                    obj.changeStatus(currentStatus, currentStatusTick)
                }
                if (changeTick) {
                    obj.currentTick = currentStatusTick
                }
            }

        }
    }
}