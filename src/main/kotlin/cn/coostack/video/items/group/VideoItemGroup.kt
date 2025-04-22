package cn.coostack.video.items.group

import cn.coostack.video.VideoParticles
import cn.coostack.video.items.VideoItems
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object VideoItemGroup {
    val VIDEO_GROUP: ItemGroup = Registry.register(
        Registries.ITEM_GROUP, Identifier.of(VideoParticles.MOD_ID, "video_group"),
        ItemGroup.Builder(null, -1)
            .displayName(Text.translatable("item.video_group"))
            .icon { ItemStack(VideoItems.circleExchangeItem) }
            .entries { _, entries ->
                VideoItems.items.forEach(entries::add)
            }
            .build()
    )


    fun init() {
        VideoParticles.logger.info("注册物品分组成功")
    }
}