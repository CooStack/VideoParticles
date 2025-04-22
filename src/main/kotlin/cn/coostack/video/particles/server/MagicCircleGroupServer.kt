package cn.coostack.video.particles.server


import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffer
import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffers
import cn.coostack.cooparticlesapi.network.particle.ServerParticleGroup
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.video.particles.client.MagicCircleGroupClient
import cn.coostack.video.util.TickHelper
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class MagicCircleGroupServer(val bindPlayer: UUID) : ServerParticleGroup(256.0) {
    // 同步子status
    var status = 0
    var duration = 70
    var current = 0

    companion object {
        private val playerGroups = ConcurrentHashMap<UUID, MagicCircleGroupServer>()

        @JvmStatic
        fun createAndPut(who: UUID): MagicCircleGroupServer {
            val magic = MagicCircleGroupServer(who)
            playerGroups[who] = magic
            return magic
        }

        @JvmStatic
        fun get(uuid: UUID) = playerGroups[uuid]

        @JvmStatic
        fun remove(uuid: UUID) = playerGroups.remove(uuid)
    }

    override fun getClientType(): Class<out ControlableParticleGroup> {
        return MagicCircleGroupClient::class.java
    }

    override fun otherPacketArgs(): Map<String, ParticleControlerDataBuffer<out Any>> {
        return mapOf(
            "bind_player" to ParticleControlerDataBuffers.uuid(bindPlayer),
            "status" to ParticleControlerDataBuffers.int(status),
            "duration" to ParticleControlerDataBuffers.int(duration),
            "currentTick" to ParticleControlerDataBuffers.int(current),
        )
    }

    override fun tick() {
        if (current++ > duration) {
            return
        }
        val player = world!!.getPlayerByUuid(bindPlayer) ?: return
        setPosOnServer(player.pos)
    }

    fun over() {
        change(
            { status = 2 },
            mapOf(
                "over" to ParticleControlerDataBuffers.empty()
            )
        )
        TickHelper.runTask(60) {
            kill()
        }
    }
}