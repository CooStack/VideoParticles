package cn.coostack.video.particles.client.sub.circle

import cn.coostack.cooparticlesapi.particles.Controlable
import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.impl.TestEndRodEffect
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import org.joml.Vector3f
import java.util.*

class MagicVarSizeSubGroup(
    private val w1: Int,
    private val w2: Int,
    private val r1: Double,
    private val r2: Double,
    private val count: Int,
    private val scale: Double,
    uuid: UUID
) : ControlableParticleGroup(uuid) {
    var color = Vector3f(1f, 1f, 1f)

    /**
     * 0 代表 放大
     * 1 代表不变
     * 2 代表变小
     * 需要上级同步
     */
    var status = 0
        private set

    /**
     * 需要上级同步
     */
    var currentTick = 0

    /**
     * 需要上级同步
     **/
    var duration = 20

    /**
     * status == 0
     */
    var statusStartTickAction: MagicVarSizeSubGroup.() -> Unit = {}

    /**
     * status == 1
     */
    var statusNormalTickAction: MagicVarSizeSubGroup.() -> Unit = {}

    /**
     * status == 1
     */
    var statusEndTickAction: MagicVarSizeSubGroup.() -> Unit = {}

    var beforeDisplayHandler: MagicVarSizeSubGroup.(locations: Map<ParticleRelativeData, RelativeLocation>) -> Unit = {}

    /**
     * 和status的状态无关的函数
     */
    var onGroupDisplayHandler: MagicVarSizeSubGroup.() -> Unit = {}

    /**
     * 不包括bindBuffer的同步
     */
    fun onTopGroupChange(status: Int?, duration: Int?, currentTick: Int?) {
        if (status != null && duration != null) {
            changeStatus(status, duration)
        }
        if (currentTick != null) {
            this.currentTick = currentTick
        }
    }

    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val locations = Math3DUtil.getCycloidGraphic(r1, r2, w1, w2, count, scale)
        val res = HashMap<ParticleRelativeData, RelativeLocation>()
        for (location in locations) {
            res[withEffect({
                ParticleDisplayer.withSingle(TestEndRodEffect(it))
            }) {
                this.color = this@MagicVarSizeSubGroup.color
            }] = location
        }
        return res
    }

    override fun beforeDisplay(locations: Map<ParticleRelativeData, RelativeLocation>) {
        locations.forEach { (particle, relativeLocation) ->
            relativeLocation.multiply(1.0 / duration)
        }
        beforeDisplayHandler(this, locations)
    }

    private val initLengthsPreDuration = HashMap<Controlable<*>, Double>()
    override fun onGroupDisplay() {
        particlesLocations.forEach {
            initLengthsPreDuration[it.key] = it.value.length()
        }
        addPreTickAction {
            if (status != 1) {
                if (currentTick++ > duration) {
                    if (status == 2) {
                        clearParticles()
                    }
                    return@addPreTickAction
                }
            }
            when (status) {
                0 -> {
                    particlesLocations.forEach {
                        val dir = it.value.normalize().multiply(initLengthsPreDuration[it.key]!!)
                        it.value.add(dir)
                    }
                    teleportTo(origin)
                    statusStartTickAction(this)
                }

                1 -> statusNormalTickAction(this)
                2 -> {
                    particlesLocations.forEach {
                        val dir = it.value.normalize().multiply(initLengthsPreDuration[it.key]!!)
                        if (it.value.length() <= dir.length()) {
                            return@forEach
                        }
                        it.value.remove(dir)
                    }
                    teleportTo(origin)
                    statusEndTickAction(this)
                }
            }
        }
        onGroupDisplayHandler(this)
    }

    fun changeStatus(statusID: Int, duration: Int) {
        if (statusID !in 0..2) {
            return
        }
        status = statusID
        if (status == 2) {
            if (this.duration == currentTick) {
                this.duration = duration
                this.currentTick = 0
            } else {
                this.duration = this.currentTick
                this.currentTick = 0
            }
        }
        // 同步tick之前做
        this.currentTick = 0
    }
}