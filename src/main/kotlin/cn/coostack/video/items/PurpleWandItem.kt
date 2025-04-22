package cn.coostack.video.items

import cn.coostack.cooparticlesapi.barrages.BarrageManager
import cn.coostack.cooparticlesapi.network.particle.ServerParticleGroupManager
import cn.coostack.cooparticlesapi.network.particle.util.ServerParticleUtil
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import cn.coostack.video.barrages.SwordBarrage
import cn.coostack.video.particles.server.MagicCircleGroupServer
import cn.coostack.video.util.TickHelper
import cn.coostack.video.util.VMathUtil
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.util.UseAction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class PurpleWandItem : Item(
    Settings()
        .maxCount(1)
        .maxDamage(720)
        .rarity(Rarity.EPIC)
) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val res = TypedActionResult.success(user.getStackInHand(hand))
        user.setCurrentHand(hand)
        if (world.isClient) {
            return res
        }
        val group = MagicCircleGroupServer.createAndPut(user.uuid)
        ServerParticleGroupManager.addParticleGroup(
            group, user.pos, user.world as ServerWorld
        )
        return res
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        if (user !is PlayerEntity) {
            return
        }
        if (world.isClient) {
            return
        }
        // 进行阶段设置
        val tick = getMaxUseTime(stack, user) - remainingUseTicks
        if (tick == 20 || tick == 40) {
            world.playSound(
                null,
                user.x,
                user.y,
                user.z,
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS,
                10.0f,
                when (tick) {
                    20 -> 1.5f
                    40 -> 3f
                    else -> 1.5f
                }
            )
        }

        if (tick == 60 || tick == 79) {
            world.playSound(
                null,
                user.x,
                user.y,
                user.z,
                SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY,
                SoundCategory.PLAYERS,
                10.0f,
                when (tick) {
                    60 -> 1.5f
                    79 -> 3f
                    else -> 1.5f
                }
            )
        }
        if (tick == 60 || tick == 79 || tick == 20 || tick == 40) {
            val circle = Math3DUtil.getCycloidGraphic(1.0, 1.0, 1, 1, 360, 6.0)
                .onEach { it.y += 8.0 }
            Math3DUtil.rotatePointsToPoint(circle, RelativeLocation.of(user.rotationVector), RelativeLocation.yAxis())
            circle.forEach {
                val pos = RelativeLocation(
                    user.eyePos.x + it.x,
                    user.eyePos.y + it.y,
                    user.eyePos.z + it.z,
                )
                val circleOrigin = RelativeLocation.of(user.rotationVector).multiply(8.0)
                val dir = circleOrigin.remove(it).normalize().multiply(0.8)
                ServerParticleUtil.spawnSingle(
                    ParticleTypes.CLOUD, world as ServerWorld, pos.toVector(), dir.toVector()
                )
            }
        }
        handleUsingTick(world as ServerWorld, user, tick)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (world.isClient) {
            return
        }
        if (user !is PlayerEntity) {
            return
        }
        val group = MagicCircleGroupServer.get(user.uuid) ?: return
        if (getMaxUseTime(stack, user) - remainingUseTicks >= 80) {
            handRelease(world as ServerWorld, user)
            group.over()
            MagicCircleGroupServer.remove(user.uuid)
            return
        }
        MagicCircleGroupServer.remove(user.uuid)
        group.over()
    }

    override fun getUseAction(stack: ItemStack?): UseAction {
        return UseAction.BOW
    }


    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        return stack
    }

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int {
        return 3600 * 20
    }


    private fun handRelease(world: ServerWorld, user: PlayerEntity) {
        user.itemCooldownManager.set(this, 50)
        world.playSound(
            null,
            user.x,
            user.y,
            user.z,
            SoundEvents.ITEM_TOTEM_USE,
            SoundCategory.PLAYERS,
            10.0f,
            1.0f
        )
        world.playSound(
            null,
            user.x,
            user.y,
            user.z,
            SoundEvents.ENTITY_IRON_GOLEM_REPAIR,
            SoundCategory.PLAYERS,
            10.0f,
            1.0f
        )
        spawnReleaseParticles(world, user)
        handleReleaseSwordBarrage(world, user)
    }


    private fun spawnReleaseParticles(world: ServerWorld, user: PlayerEntity) {
        for (loc in VMathUtil.getBallLocations(2.0, 16)) {
            ServerParticleUtil.spawnSingle(ParticleTypes.FLAME, world, user.eyePos, loc.toVector())
        }
        val cylinder = VMathUtil.getCylinderLocations(120.0, 4.0, 12.0, 2.0, 36, 72)
        Math3DUtil.rotatePointsToPoint(cylinder, RelativeLocation.of(user.rotationVector), RelativeLocation.yAxis())
        for (loc in cylinder) {
            ServerParticleUtil.spawnSingle(ParticleTypes.CLOUD, world, user.eyePos.add(loc.toVector()), Vec3d.ZERO)
        }
    }

    private fun handleReleaseSwordBarrage(world: ServerWorld, user: PlayerEntity) {
        val rounds = Math3DUtil.getRoundScapeLocations(12.0, 0.5, 36, 360)
        Math3DUtil.rotatePointsToPoint(rounds, RelativeLocation.of(user.rotationVector), RelativeLocation.yAxis())
        val direction = user.rotationVector
        val pos = user.eyePos
        TickHelper.runTaskTimerMaxTick(120) {
            for (i in 0..<4) {
                val barrage = SwordBarrage(
                    pos.add(rounds.random().toVector()), world, direction
                ) {
                    it.uuid != user.uuid
                }
                barrage.shooter = user
                barrage.direction = direction
                BarrageManager.spawn(barrage)
            }
        }

    }

    private fun handleUsingTick(world: ServerWorld, user: PlayerEntity, using: Int) {
        if (using % 2 == 0) {
            val direction = user.rotationVector
            val rounds = Math3DUtil.getRoundScapeLocations(6.0, 0.5, 36, 360)
            Math3DUtil.rotatePointsToPoint(rounds, RelativeLocation.of(user.rotationVector), RelativeLocation.yAxis())
            val barrage = SwordBarrage(
                user.eyePos.add(rounds.random().toVector()), world, direction, {
                    it.uuid != user.uuid
                }
            )
            barrage.shooter = user
            barrage.direction = direction
            BarrageManager.spawn(barrage)
        }
    }

}