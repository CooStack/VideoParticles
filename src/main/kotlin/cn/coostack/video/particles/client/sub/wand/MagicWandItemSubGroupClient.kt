package cn.coostack.video.particles.client.sub.wand

import cn.coostack.cooparticlesapi.particles.Controlable
import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.impl.TestEndRodEffect
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import org.joml.Vector3f
import java.util.*
import kotlin.collections.HashMap

class MagicWandItemSubGroupClient(uuid: UUID) : ControlableParticleGroup(uuid) {
    var r1 = 1.0
    var r2 = 1.0
    var w1 = 1
    var w2 = 1
    var count = 720
    var scale = 1.0
    var color = Vector3f(1f, 1f, 1f)
    var withDisplay: MagicWandItemSubGroupClient.() -> Unit = {}
    var withBeforeDisplay: MagicWandItemSubGroupClient.(Map<ParticleRelativeData, RelativeLocation>) -> Unit = {}
    val buffer = HashMap<String, Any>()
    private var ct = 0
    private var maxCT = 20
    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val locations = Math3DUtil.getCycloidGraphic(r1, r2, w1, w2, count, scale)
        val res = HashMap<ParticleRelativeData, RelativeLocation>()
        for (location in locations) {
            res[withEffect({
                ParticleDisplayer.withSingle(TestEndRodEffect(it))
            }) {
                this.color = this@MagicWandItemSubGroupClient.color
            }] = location
        }

        return res
    }

    val firstClone = HashMap<Controlable<*>, Double>()

    override fun beforeDisplay(locations: Map<ParticleRelativeData, RelativeLocation>) {
        withBeforeDisplay(this, locations)
    }

    override fun onGroupDisplay() {
        withDisplay(this)
        val clone = particlesLocations.map {
            it.key to it.value.length() / maxCT
        }.toMap()
        particlesLocations.forEach {
            it.value.multiply(1.0 / maxCT)
        }
        firstClone.putAll(clone)
    }

    fun toggleTick(ct: Int) {
        if (ct > maxCT) {
            this.ct = maxCT
        }
        this.ct = ct
    }

    fun doTick() {
        if (ct++ > maxCT) {
            return
        }
        particlesLocations.forEach {
            val len = firstClone[it.key]!!
            it.value.add(it.value.normalize().multiply(len))
        }
    }
}