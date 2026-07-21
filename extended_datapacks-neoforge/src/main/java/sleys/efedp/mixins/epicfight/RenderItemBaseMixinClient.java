package sleys.efedp.mixins.epicfight;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sleys.efedp.system.weapons.json.WeaponAdvancedSwingTrail;
import sleys.sl.epicfight.capability.AnimationRenderContext;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;

@Mixin(RenderItemBase.class)
public class RenderItemBaseMixinClient {

    @Inject(method = "trailInfo", at = @At("RETURN"), cancellable = true, remap = false)
    private void modifyTrailInfo(CallbackInfoReturnable<TrailInfo> cir) {
        TrailInfo original = cir.getReturnValue();
        if (original == null) return;

        LivingEntityPatch<?> entityPatch = AnimationRenderContext.getCurrentEntityPatch();
        if (entityPatch == null) return;

        LivingEntity entity = entityPatch.getOriginal();
        InteractionHand hand = original.hand() != null
                        ? original.hand()
                        : InteractionHand.MAIN_HAND;

        ItemStack item = entity.getItemInHand(hand);
        WeaponAdvancedSwingTrail.AdvancedSwingTrails config = WeaponAdvancedSwingTrail.getAdvancedSwingTrails(item);

        if (config == null) return;
        WeaponAdvancedSwingTrail.SwingTrail trail = extended_datapacks$resolveTrail(config, entityPatch);

        if (trail == null) return;
        cir.setReturnValue(
                extended_datapacks$createModifiedTrailInfo(
                        original,
                        trail
                )
        );
    }

    @Unique
    private WeaponAdvancedSwingTrail.SwingTrail extended_datapacks$resolveTrail(
            WeaponAdvancedSwingTrail.AdvancedSwingTrails config, LivingEntityPatch<?> entityPatch) {

        CapabilityItem capability = entityPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
        if (capability == null) return null;

        Style currentStyle = capability.getStyle(entityPatch);
        return config.getTrailForStyle(currentStyle);
    }

    @Unique
    private TrailInfo extended_datapacks$createModifiedTrailInfo(TrailInfo original,
                                                                 WeaponAdvancedSwingTrail.SwingTrail trail) {
        return new TrailInfo(
                trail.start(),
                trail.end(),
                original.joint(),
                trail.particle(),

                original.startTime(),
                original.endTime(),
                original.fadeTime(),

                trail.color().rFloat(),
                trail.color().gFloat(),
                trail.color().bFloat(),

                trail.interpolateCount(),
                trail.trailLifetime(),

                original.updateInterval(),
                original.blockLight(),
                original.skyLight(),

                trail.texturePath(),
                original.hand()
        );
    }
}
