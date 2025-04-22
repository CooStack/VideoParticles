package cn.coostack.video.items

import cn.coostack.video.VideoParticles
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object VideoItems {
    val items = ArrayList<Item>()

    @JvmField
    val circleExchangeItem = register("circle_exchange_item", CircleExchangeItem())

    @JvmField
    val pinkWandItem = register("pink_wand", PinkWandItem())

    @JvmField
    val purpleWandItem = register("purple_wand", PurpleWandItem())

    @JvmField
    val redWandItem = register("red_wand", RedWandItem())
    fun register(id: String, item: Item): Item {
        val res = Registry.register(
            Registries.ITEM, Identifier.of(VideoParticles.MOD_ID, id), item
        )
        items.add(res)
        return res
    }

    @JvmStatic
    fun init() {
        VideoParticles.logger.info("注册物品成功")
    }

}