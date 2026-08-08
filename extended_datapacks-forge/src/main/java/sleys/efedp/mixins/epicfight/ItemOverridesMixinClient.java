package sleys.efedp.mixins.epicfight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sleys.efedp.capability.StyleWrappedBakedModel;
import sleys.efedp.system.weapons.WeaponsModelsRegistry;
import sleys.efedp.system.weapons.json.WeaponPerStyleModelBakerBuilder;
import sleys.sl.epicfight.capability.StyleInvalid;
import sleys.sl.epicfight.util.helper.patch.PatchPlayerHelper;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;

import javax.annotation.Nullable;

@Mixin(ItemOverrides.class)
public class ItemOverridesMixinClient {

    @Inject(
            method = "resolve",
            at = @At("HEAD"),
            cancellable = true
    )
    private void resolveOverride(BakedModel originalModel, ItemStack stack,
                                 @Nullable ClientLevel level, @Nullable LivingEntity entity,
                                 int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!(entity instanceof Player player)) return;

        PlayerPatch<?> playerPatch = EpicFightCapabilities.getPlayerPatch(player);
        if (!PatchPlayerHelper.isValidPatchedPlayer(playerPatch)) return;

        WeaponPerStyleModelBakerBuilder.WeaponModelPerStyle config = WeaponPerStyleModelBakerBuilder.getModelStyleItems(stack);
        if (!config.hasModels()) return;

        BakedModel modelSocket = extended_datapacks$getModelStyleSocket(config, playerPatch, stack);
        if (modelSocket == null) return;

        if (modelSocket != Minecraft.getInstance().getModelManager().getMissingModel()) {
            BakedModel wrapped = new StyleWrappedBakedModel(originalModel, modelSocket);
            cir.setReturnValue(wrapped);
        }
    }

    @Unique
    private static BakedModel extended_datapacks$getModelStyleSocket(WeaponPerStyleModelBakerBuilder.WeaponModelPerStyle config,
                                                                     PlayerPatch<?> playerPatch, ItemStack stack) {
        final boolean isMainHanded = playerPatch.getOriginal()
                .getItemInHand(InteractionHand.MAIN_HAND)
                .getItem().equals(stack.getItem());
        if (!isMainHanded) return null;

        CapabilityItem currentCapability = playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
        if (currentCapability == null) return null;

        Style currentStyle = currentCapability.getStyle(playerPatch);
        if (currentStyle == null || currentStyle.equals(StyleInvalid.INVALID)) return null;

        ResourceLocation currentModel = config.getModel(currentStyle);
        if (currentModel == null) return null;

        ModelResourceLocation redirectedModel = WeaponsModelsRegistry.getModelResourceLocation(currentModel);
        if (redirectedModel == null) return null;

        return Minecraft.getInstance().getItemRenderer()
                .getItemModelShaper()
                .getModelManager()
                .getModel(redirectedModel);
    }
}