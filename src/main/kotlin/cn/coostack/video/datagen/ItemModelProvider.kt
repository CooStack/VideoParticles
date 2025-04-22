package cn.coostack.video.datagen

import cn.coostack.video.VideoParticles
import cn.coostack.video.items.VideoItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models
import net.minecraft.util.Identifier

class ItemModelProvider(output: FabricDataOutput?) : FabricModelProvider(output) {
    override fun generateBlockStateModels(p0: BlockStateModelGenerator?) {
    }

    override fun generateItemModels(gen: ItemModelGenerator) {
        gen.register(VideoItems.circleExchangeItem, Models.HANDHELD)
        gen.register(VideoItems.pinkWandItem, Models.HANDHELD)
        gen.register(VideoItems.redWandItem, Models.HANDHELD)
        gen.register(VideoItems.purpleWandItem, Models.HANDHELD)
    }
}