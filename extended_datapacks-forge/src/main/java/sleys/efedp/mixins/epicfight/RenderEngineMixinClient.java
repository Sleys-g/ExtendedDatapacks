package sleys.efedp.mixins.epicfight;

import com.google.gson.JsonElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.weapons.json.WeaponAdvancedSwingTrail;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

import java.util.Map;

@Mixin(RenderEngine.class)
public class RenderEngineMixinClient {

    @Final @Shadow(remap = false)
    private Map<Item, RenderItemBase> itemRendererMapByInstance;

    @SuppressWarnings("deprecation")
    @Inject(
            method = "reloadItemRenderers",
            at = @At("TAIL"),
            remap = false
    )
    private void modifyReloadItemRenderes(Map<ResourceLocation, JsonElement> objects, CallbackInfo ci) {
        RenderItemBase fallbackRenderer = null;
        for (RenderItemBase renderer : this.itemRendererMapByInstance.values()) {
            fallbackRenderer = renderer;
            break;
        }

        if (fallbackRenderer == null) {
            return;
        }

        for (ResourceLocation registryName : WeaponAdvancedSwingTrail.getRegisteredItems()) {
            Item item = BuiltInRegistries.ITEM.get(registryName);

            if (item == Items.AIR) {
                ExtendedDatapacks.LOGGER.warn(
                        "[Advanced Swing Trails] Failed to load advanced swing trail for item: {}",
                        registryName
                );
                continue;
            } else {
                ExtendedDatapacks.LOGGER.info(
                        "[Advanced Swing Trails] Successful registration of Advanced Swing Trails for the item: {}",
                        registryName
                );
            }

            this.itemRendererMapByInstance.putIfAbsent(
                    item,
                    fallbackRenderer
            );
        }
    }
}
