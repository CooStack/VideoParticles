package cn.coostack.video

import cn.coostack.video.datagen.ItemModelProvider
import cn.coostack.video.datagen.LangProvider
import cn.coostack.video.datagen.RecipeProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator


object VideoParticlesDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val modId = fabricDataGenerator.modId
        if (modId != VideoParticles.MOD_ID) return
        val createPack = fabricDataGenerator.createPack()
        createPack.addProvider(::ItemModelProvider)
        createPack.addProvider(::LangProvider)
        createPack.addProvider(::RecipeProvider)
    }
}