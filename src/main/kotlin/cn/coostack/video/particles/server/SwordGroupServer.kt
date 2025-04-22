package cn.coostack.video.particles.server

import cn.coostack.cooparticlesapi.barrages.HitBox
import cn.coostack.cooparticlesapi.network.buffer.IntControlerBuffer
import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffer
import cn.coostack.cooparticlesapi.network.buffer.ParticleControlerDataBuffers
import cn.coostack.cooparticlesapi.network.particle.ServerParticleGroup
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.video.particles.client.SwordGroupClient
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Vec3d

class SwordGroupServer(val filter: (LivingEntity) -> Boolean, val searchBox: HitBox) : ServerParticleGroup(64.0) {
    var direction = Vec3d.ZERO
    var rotateStart = 0.0
    var rotateDirection = -1
    override fun tick() {
    }


    override fun otherPacketArgs(): Map<String, ParticleControlerDataBuffer<out Any>> {
        return mapOf(
            "direction" to ParticleControlerDataBuffers.vec3d(direction),
            "rotateStart" to ParticleControlerDataBuffers.double(rotateStart),
            "rotateDirection" to ParticleControlerDataBuffers.int(rotateDirection)
        )
    }

    override fun getClientType(): Class<out ControlableParticleGroup>? {
        return SwordGroupClient::class.java
    }

    private fun findNearestEntityId(): Int? {
        val min = world!!.getEntitiesByClass(
            LivingEntity::class.java,
            searchBox.ofBox(
                pos
            ), filter
        ).minByOrNull {
            it.pos.distanceTo(pos)
        }

        return min?.id
    }
}