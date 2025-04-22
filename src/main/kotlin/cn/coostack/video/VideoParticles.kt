package cn.coostack.video

import cn.coostack.video.items.CircleExchangeItem
import cn.coostack.video.items.VideoItems
import cn.coostack.video.items.group.VideoItemGroup
import cn.coostack.video.particles.server.MagicWandGroupServer
import cn.coostack.video.util.TickHelper
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import org.slf4j.LoggerFactory

object VideoParticles : ModInitializer {
    const val MOD_ID = "videoparticles"
    val logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        VideoItems.init()
        VideoItemGroup.init()

        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            val player = handler.getPlayer() ?: return@register
            CircleExchangeItem.playerCircles.remove(player.uuid)
            MagicWandGroupServer.remove(player.uuid)
        }

        ServerTickEvents.START_SERVER_TICK.register {
            TickHelper.doTick()
        }
    }
}