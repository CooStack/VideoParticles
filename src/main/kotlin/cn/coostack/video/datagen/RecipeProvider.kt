package cn.coostack.video.datagen

import cn.coostack.video.VideoParticles
import cn.coostack.video.items.VideoItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.data.server.recipe.RecipeProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class RecipeProvider(output: FabricDataOutput?, registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>?) :
    FabricRecipeProvider(output, registriesFuture) {
    override fun generate(exporter: RecipeExporter) {
        ShapedRecipeJsonBuilder.create(
            RecipeCategory.COMBAT,
            VideoItems.pinkWandItem
        )
            .pattern("DNN")
            .pattern("DBS")
            .pattern("BN ")
            .input('D', Ingredient.ofItems(Items.DIAMOND_BLOCK))
            .input('N', Ingredient.ofItems(Items.NETHERITE_BLOCK))
            .input('S', Ingredient.ofItems(Items.NETHER_STAR))
            .input('B', Ingredient.ofItems(Items.BLAZE_ROD))
            .criterion("has_item", RecipeProvider.conditionsFromItem(Items.NETHER_STAR))
            .offerTo(exporter, Identifier.of(VideoParticles.MOD_ID, "pin_wand_item_recipe"))

        ShapedRecipeJsonBuilder.create(
            RecipeCategory.COMBAT,
            VideoItems.redWandItem
        )
            .pattern("NNN")
            .pattern("NDS")
            .pattern("DNR")
            .input('D', Ingredient.ofItems(Items.DIAMOND_BLOCK))
            .input('N', Ingredient.ofItems(Items.NETHERITE_BLOCK))
            .input('S', Ingredient.ofItems(Items.NETHER_STAR))
            .input('R', Ingredient.ofItems(Items.REDSTONE_BLOCK))
            .criterion("has_item", RecipeProvider.conditionsFromItem(Items.NETHER_STAR))
            .offerTo(exporter, Identifier.of(VideoParticles.MOD_ID, "red_wand_item_recipe"))
    }
}