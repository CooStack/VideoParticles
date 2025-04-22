package cn.coostack.video.particles.client.sub

import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.impl.TestEndRodEffect
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import org.joml.Vector3f
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

class MagicGroupClient(uuid: UUID) : ControlableParticleGroup(uuid) {
    var r1 = 1.0
    var r2 = 1.0
    var w1 = 1
    var w2 = 1
    var count = 720
    var scale = 1.0
    var color = Vector3f(1f, 1f, 1f)
    var withDisplay: MagicGroupClient.() -> Unit = {}
    private var ct = 0
    private var maxCT = 60
    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val locations = Math3DUtil.getCycloidGraphic(r1, r2, w1, w2, count, scale)
        val res = HashMap<ParticleRelativeData, RelativeLocation>()
        for (location in locations) {
            res[withEffect({
                ParticleDisplayer.withSingle(TestEndRodEffect(it))
            }) {
                this.color = this@MagicGroupClient.color
            }] = location
        }
        return res
    }

    override fun onGroupDisplay() {
        withDisplay(this)
        val firstClone = particlesLocations.map {
            it.key to it.value.length() / maxCT
        }.toMap()
        particlesLocations.forEach {
            it.value.multiply(1.0 / maxCT)
        }
        addPreTickAction {
            if (ct++ > maxCT) {
                return@addPreTickAction
            }
            particlesLocations.forEach {
                val len = firstClone[it.key]!!
                it.value.add(it.value.normalize().multiply(len))
            }
        }
    }
}