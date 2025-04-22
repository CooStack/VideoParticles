package cn.coostack.video.datagen

import cn.coostack.video.items.VideoItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class LangProvider(
    dataOutput: FabricDataOutput?,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>?
) : FabricLanguageProvider(dataOutput, "zh_cn", registryLookup) {
    override fun generateTranslations(loopup: RegistryWrapper.WrapperLookup?, builder: TranslationBuilder) {
        builder.apply {
            add("item.video_group", "视频物品")
            add(VideoItems.circleExchangeItem, "圆粒子控制器")
            add(VideoItems.pinkWandItem, "粉红魔杖")
            add(VideoItems.redWandItem, "猩红魔杖")
            add(VideoItems.purpleWandItem, "紫色魔杖")
        }
    }
}