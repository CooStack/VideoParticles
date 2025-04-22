package cn.coostack.video.barrages

import cn.coostack.cooparticlesapi.barrages.AbstractBarrage
import cn.coostack.cooparticlesapi.barrages.Barrage
import cn.coostack.cooparticlesapi.barrages.BarrageHitResult
import cn.coostack.cooparticlesapi.barrages.BarrageOption
import cn.coostack.cooparticlesapi.barrages.HitBox
import cn.coostack.video.particles.server.SwordGroupServer
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.PI
import kotlin.random.Random

class SwordBarrage(
    loc: Vec3d,
    world: ServerWorld,
    firstDirection: Vec3d,
    val entityFilter: (LivingEntity) -> Boolean,
) : AbstractBarrage(
    loc, world, HitBox.of(3.0, 3.0, 3.0),
    SwordGroupServer(
        entityFilter,
        HitBox.of(60.0, 60.0, 60.0)
    ).apply {
        direction = firstDirection
        rotateStart = Random(System.currentTimeMillis()).nextDouble(0.0, PI)
        rotateDirection = if(Random(System.currentTimeMillis()).nextBoolean()) 1 else 0
    }, BarrageOption()
        .apply {
            enableSpeed = true
            speed = 2.0
        }
) {
    override fun filterHitEntity(livingEntity: LivingEntity): Boolean {
        return entityFilter(livingEntity)
    }


    override fun onHit(result: BarrageHitResult) {
        handleBlock(result.hitBlockState)
        handleEntities(result.entities)
    }

    private fun handleBlock(state: BlockState?) {
        state ?: return
        val source = shooter?.damageSources?.mobAttack(shooter) ?: world.damageSources.explosion(null)
        world.createExplosion(shooter, source, null, loc, 2f, false, World.ExplosionSourceType.TNT)
    }

    private fun handleEntities(entities: List<LivingEntity>) {
        val source = shooter?.damageSources?.mobAttack(shooter) ?: world.damageSources.campfire()
        entities.forEach {
            it.damage(source, 5f)
        }
        val sourceExplosion = shooter?.damageSources?.mobAttack(shooter) ?: world.damageSources.explosion(null)
        world.createExplosion(shooter, sourceExplosion, null, loc, 2f, false, World.ExplosionSourceType.TNT)
    }
}