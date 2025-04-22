package cn.coostack.video.particles.client.sub.circle

import cn.coostack.cooparticlesapi.particles.ParticleDisplayer
import cn.coostack.cooparticlesapi.particles.control.group.ControlableParticleGroup
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import org.joml.Vector3f
import java.util.*
import kotlin.math.PI

class MagicBottomSubGroup(uuid: UUID) : ControlableParticleGroup(uuid) {
    override fun loadParticleLocations(): Map<ParticleRelativeData, RelativeLocation> {
        val result = HashMap<ParticleRelativeData, RelativeLocation>()
        val statusDuration = 60
        result[
            withEffect({
                ParticleDisplayer.withGroup(
                    MagicVarSizeSubGroup(
                        w1 = -3,
                        w2 = 7,
                        r1 = 2.0,
                        r2 = 5.0,
                        count = 360 * 7,
                        scale = 1.0,
                        uuid = it
                    ).also { g ->
                        g.changeStatus(0, statusDuration)
                        g.onGroupDisplayHandler = {
                            addPreTickAction {
                                rotateParticlesAsAxis(PI / 72)
                            }
                        }
                        g.color = Vector3f(247 / 255f, 120 / 255f, 243 / 255f)
                    }
                )
            }) {}
        ] = RelativeLocation()

        result[
            withEffect({
                ParticleDisplayer.withGroup(
                    MagicVarSizeSubGroup(
                        w1 = -2,
                        w2 = 3,
                        r1 = 3.0,
                        r2 = 4.0,
                        count = 360 * 3,
                        scale = 1.0,
                        uuid = it
                    ).also { g ->
                        g.changeStatus(0, statusDuration)
                        g.onGroupDisplayHandler = {
                            addPreTickAction {
                                rotateParticlesAsAxis(-PI / 72)
                            }
                        }
                        g.color = Vector3f(147 / 255f, 15 / 255f, 248 / 255f)
                    }
                )
            }) {}
        ] = RelativeLocation()
        result[
            withEffect({
                ParticleDisplayer.withGroup(
                    MagicVarSizeSubGroup(
                        w1 = 1,
                        w2 = 1,
                        r1 = 1.0,
                        r2 = 1.0,
                        count = 360 * 2,
                        scale = 3.0,
                        uuid = it
                    ).also { g ->
                        g.changeStatus(0, statusDuration)
                        g.onGroupDisplayHandler = {
                            addPreTickAction {
                                rotateParticlesAsAxis(-PI / 36)
                            }
                        }
                        g.color = Vector3f(133 / 255f, 109 / 255f, 239 / 255f)
                    }
                )
            }) {}
        ] = RelativeLocation()
        result[
            withEffect({
                ParticleDisplayer.withGroup(
                    MagicVarSizeSubGroup(
                        w1 = 1,
                        w2 = 1,
                        r1 = 1.0,
                        r2 = 1.0,
                        count = 360 * 3,
                        scale = 5.0,
                        uuid = it
                    ).also { g ->
                        g.changeStatus(0, statusDuration)
                        g.onGroupDisplayHandler = {
                            addPreTickAction {
                                rotateParticlesAsAxis(PI / 36)
                            }
                        }
                        g.color = Vector3f(133 / 255f, 109 / 255f, 239 / 255f)
                    }
                )
            }) {}
        ] = RelativeLocation()
        return result
    }

    override fun onGroupDisplay() {
    }

    fun over() {
        particlesLocations.forEach { t, u ->
            val obj = t.getControlObject()
            if (obj is MagicVarSizeSubGroup) {
                obj.changeStatus(2, 60)
            }
        }
    }

    fun onChange(status: Int?, duration: Int?, currentTick: Int?) {
        particlesLocations.forEach { t, u ->
            val controlObject = t.getControlObject()
            if (controlObject !is MagicVarSizeSubGroup) {
                return@forEach
            }
            controlObject.onTopGroupChange(status, duration, currentTick)
        }
    }
}