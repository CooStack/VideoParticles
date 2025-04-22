package cn.coostack.video.items

import cn.coostack.cooparticlesapi.network.particle.ServerParticleGroupManager
import cn.coostack.cooparticlesapi.network.particle.util.ServerParticleUtil
import cn.coostack.cooparticlesapi.particles.control.group.ClientParticleGroupManager
import cn.coostack.cooparticlesapi.utils.Math3DUtil
import cn.coostack.cooparticlesapi.utils.RelativeLocation
import cn.coostack.video.particles.client.MagicWandGroupClient
import cn.coostack.video.particles.server.MagicWandGroupServer
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
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.UUID
import kotlin.math.cos
import kotlin.random.Random

class PinkWandItem : Item(
    Settings()
        .maxDamage(1500)
        .maxCount(1)
        .rarity(Rarity.EPIC)
        .fireproof()
) {

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val item = user.getStackInHand(hand)
        if (world.isClient) {
            return TypedActionResult.success(item)
        }
        val group = MagicWandGroupServer.createAndPut(user.uuid)
        ServerParticleGroupManager.addParticleGroup(
            group, user.eyePos, user.world as ServerWorld
        )
        user.setCurrentHand(hand)
        return TypedActionResult.consume(item)
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        if (world.isClient) {
            return
        }
//        val ticks = getMaxUseTime(stack, user) - remainingUseTicks
        if (user !is PlayerEntity) {
            return
        }
        val group = MagicWandGroupServer.get(user.uuid) ?: return
        group.kill()
        MagicWandGroupServer.remove(user.uuid)
    }

    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        val group = MagicWandGroupServer.get(user.uuid) ?: return stack
        val v = user.rotationVector.normalize().multiply(-2.0)
        if (world.isClient) {
            return stack
        }
        world as ServerWorld
        for (loc in VMathUtil.getBallLocations(2.0, 16)) {
            ServerParticleUtil.spawnSingle(ParticleTypes.FLAME, world, group.pos, loc.toVector())
        }

        val cylinder = VMathUtil.getCylinderLocations(90.0, 4.0, 12.0, 2.0, 36, 72)
        Math3DUtil.rotatePointsToPoint(cylinder, RelativeLocation.of(user.rotationVector), RelativeLocation.yAxis())
        for (loc in cylinder) {
            ServerParticleUtil.spawnSingle(ParticleTypes.CLOUD, world, group.pos.add(loc.toVector()), Vec3d.ZERO)
        }

        group.over()
        val level = 15f
        for ((current, loc) in Math3DUtil.getLineLocations(
            group.pos, group.pos.add(user.rotationVector.normalize().multiply(90.0)), 15
        ).withIndex()) {
            world.createExplosion(
                user,
                loc.x,
                loc.y,
                loc.z,
                level,
                World.ExplosionSourceType.TNT
            )
            world.getEntitiesByClass(
                LivingEntity::class.java,
                Box.of(loc.toVector(), 24.0, 24.0, 24.0)
            ) {
                return@getEntitiesByClass it.uuid != user.uuid
            }.forEach {
                it.damage(user.damageSources.mobAttack(user), 10f)
            }
        }
        user.velocity = v
        user.velocityModified = true
        world.playSound(
            null,
            user.x,
            user.y,
            user.z,
            SoundEvents.ENTITY_IRON_GOLEM_DEATH,
            SoundCategory.PLAYERS,
            1.0f,
            1.0f
        )
        world.playSound(
            null,
            user.x,
            user.y,
            user.z,
            SoundEvents.ENTITY_IRON_GOLEM_REPAIR,
            SoundCategory.PLAYERS,
            1.0f,
            1.0f
        )
        if (user is PlayerEntity) {
            user.itemCooldownManager.set(this, 20)
        }
        return stack
    }


    override fun getUseAction(stack: ItemStack): UseAction {
        return UseAction.BOW
    }


    override fun isUsedOnRelease(stack: ItemStack?): Boolean {
        return super.isUsedOnRelease(stack)
    }

    override fun usageTick(world: World, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        // 发送粒子更新包 update_tick
        if (user !is PlayerEntity) {
            return
        }
        if (world.isClient) {
            return
        }
        val group = MagicWandGroupServer.get(user.uuid) ?: return
        world as ServerWorld
        val tick = getMaxUseTime(stack, user) - remainingUseTicks
        if (tick == 20 || tick == 40 || tick == 60) {
            world.playSound(
                null, user.x, user.y, user.z, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1f, 1f
            )
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
                val dir = circleOrigin.remove(it).normalize().multiply(2.0)
                ServerParticleUtil.spawnSingle(
                    ParticleTypes.SMOKE, world, pos.toVector(), dir.toVector()
                )
            }
        }

        group.doTick()
        val seeDirection = user.rotationVector.normalize()
        val eye = user.eyePos.add(
            seeDirection.multiply(8.0)
        )
        val random = Random(System.currentTimeMillis())
        for (i in 0..8) {
            val x = cos(Math.toRadians(random.nextDouble() * 360)) * random.nextInt(5, 9)
            val z = cos(Math.toRadians(random.nextDouble() * 360)) * random.nextInt(5, 9)
            val rel = RelativeLocation(x, -2.0, z)
            Math3DUtil.rotatePointsToPoint(
                listOf(rel),
                RelativeLocation.of(seeDirection),
                RelativeLocation(0.0, 1.0, 0.0)
            )
            val pos = user.pos.add(rel.toVector())
            val targetDirection = pos.relativize(eye).normalize().multiply(0.4)
            ServerParticleUtil.spawnSingle(
                ParticleTypes.CLOUD, world, pos, targetDirection
            )
        }
    }

    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?): Int {
        return 80
    }

}