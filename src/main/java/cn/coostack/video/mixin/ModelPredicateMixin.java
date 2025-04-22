package cn.coostack.video.mixin;

import cn.coostack.video.VideoParticles;
import cn.coostack.video.items.VideoItems;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPredicateProviderRegistry.class)
abstract class ModelPredicateMixin {

    @Shadow
    static void register(Item item, Identifier id, ClampedModelPredicateProvider provider) {
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        register(VideoItems.pinkWandItem, Identifier.of(VideoParticles.MOD_ID, "pulling"),
                (stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? entity.getItemUseTimeLeft() % 10 / 10.0F : 0.0F);
    }

}
