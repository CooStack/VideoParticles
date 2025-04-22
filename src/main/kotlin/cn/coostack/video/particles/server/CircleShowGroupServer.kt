package cn.coostack.video.particles.server

import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffer
import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffers
import cn.coostack.cooparticlesapi.network.particle.ServerParticleGroup
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.video.particles.client.CircleShowGroupClient
import java.util.UUID

class CircleShowGroupServer(val bindPlayer: UUID) : ServerParticleGroup() {
    var status = 1
        private set

    override fun getClientType(): Class<out ControlableParticleGroup> = CircleShowGroupClient::class.java


    override fun otherPacketArgs(): Map<String, ParticleControlerDataBuffer<out Any>> {
        return mapOf(
            "status" to ParticleControlerDataBuffers.int(status),
            "bind_player" to ParticleControlerDataBuffers.uuid(bindPlayer)
        )
    }

    override fun tick() {
        val player = world!!.getPlayerByUuid(bindPlayer) ?: return
        setPosOnServer(player.pos)
    }

    fun changeStatus(new: Int) {
        if (new !in 1..5) {
            return
        }
        change({
            status = new
        }, mapOf("status" to ParticleControlerDataBuffers.int(new), "flush" to ParticleControlerDataBuffers.int(0)))
    }
}