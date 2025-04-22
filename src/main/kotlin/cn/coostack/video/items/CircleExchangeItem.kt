package cn.coostack.video.items

import cn.coostack.cooparticlesapi.network.particle.ServerParticleGroupManager
import cn.coostack.video.particles.server.CircleShowGroupServer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import java.util.UUID

class CircleExchangeItem : Item(Settings()) {
    companion object {
        val playerCircles = HashMap<UUID, CircleShowGroupServer>()
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val item = user.getStackInHand(hand)
        if (world.isClient) {
            return TypedActionResult.success(item)
        }

        val serverUser = user as ServerPlayerEntity
        val uuid = serverUser.uuid
        if (!playerCircles.containsKey(uuid)) {
            // 生成
            val group = CircleShowGroupServer(user.uuid)
            playerCircles[uuid] = group
            ServerParticleGroupManager.addParticleGroup(
                group, user.pos, world as ServerWorld
            )
        } else {
            // status++
            val group = playerCircles[uuid]!!
            if (group.status == 5) {
                group.changeStatus(1)
            } else {
                group.changeStatus(group.status + 1)
            }
        }

        return TypedActionResult.success(item)
    }

}