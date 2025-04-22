package cn.coostack.video

import cn.coostack.cooparticlesapi.particles.control.group.ClientParticleGroupManager
import cn.coostack.video.particles.client.CircleShowGroupClient
import cn.coostack.video.particles.client.MagicCircleGroupClient
import cn.coostack.video.particles.client.MagicWandGroupClient
import cn.coostack.video.particles.client.SwordGroupClient
import net.fabricmc.api.ClientModInitializer

object VideoParticlesClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientParticleGroupManager.register(
            CircleShowGroupClient::class.java, CircleShowGroupClient.Provider()
        )
        ClientParticleGroupManager.register(
            MagicWandGroupClient::class.java, MagicWandGroupClient.Provider()
        )
        ClientParticleGroupManager.register(
            MagicCircleGroupClient::class.java, MagicCircleGroupClient.Provider()
        )

        ClientParticleGroupManager.register(
            SwordGroupClient::class.java, SwordGroupClient.Provider()
        )
    }

}