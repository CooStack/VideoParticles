package cn.coostack.video.particles.server

import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffer
import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffers
import cn.coostack.cooparticlesapi.network.particle.ServerParticleGroup
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.video.items.VideoItems
import cn.coostack.video.particles.client.MagicWandGroupClient
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class MagicWandGroupServer(val bindPlayer: UUID) : ServerParticleGroup(256.0) {
    companion object {
        private val playerGroups = ConcurrentHashMap<UUID, MagicWandGroupServer>()

        @JvmStatic
        fun createAndPut(who: UUID): MagicWandGroupServer {
            val magic = MagicWandGroupServer(who)
            playerGroups[who] = magic
            return magic
        }

        @JvmStatic
        fun get(uuid: UUID) = playerGroups[uuid]

        @JvmStatic
        fun remove(uuid: UUID) = playerGroups.remove(uuid)
    }

    override fun getClientType(): Class<out ControlableParticleGroup>? {
        return MagicWandGroupClient::class.java
    }

    override fun otherPacketArgs(): Map<String, ParticleControlerDataBuffer<out Any>> {
        return mapOf(
            "bind_player" to ParticleControlerDataBuffers.uuid(bindPlayer),
            "tick" to ParticleControlerDataBuffers.int(tick),
            "toggle_ct" to ParticleControlerDataBuffers.int(tick)
        )
    }

    override fun tick() {
        val player = world!!.getPlayerByUuid(bindPlayer) ?: return
        teleportGroupTo(player.eyePos)
        // 当玩家点太快时 可能会导致 group没来得及被kill就被覆盖
        val magicWandGroupServer = playerGroups[bindPlayer] ?: return
        if (magicWandGroupServer != this) {
            kill()
        }

        if (!player.activeItem.isOf(VideoItems.pinkWandItem)) {
            kill()
        }
    }

    fun doTick() {
        tick++
        change({}, mapOf("do_tick" to ParticleControlerDataBuffers.empty()))
    }

    fun over() {
        change({}, mapOf("over" to ParticleControlerDataBuffers.int(1)))
    }
}