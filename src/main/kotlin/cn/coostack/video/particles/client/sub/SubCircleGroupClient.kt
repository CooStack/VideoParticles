package cn.coostack.video.particles.client.sub

import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.particles.impl.TestEndRodEffect
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import org.joml.Vector3f
import java.util.*

class SubCircleGroupClient(uuid: UUID, private val scale: Double) : ControlableParticleGroup(uuid) {
    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val locations = Math3DUtil.getCycloidGraphic(1.0, 1.0, 1, 1, 360, scale)
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

    override fun onGroupDisplay() {
    }
}