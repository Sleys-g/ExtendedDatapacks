package sleys.efedp.mixins.wom;

import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.skill.weaponinnate.SolarArcanaSkill;
import sleys.efedp.capability.ExtendedDatapacksUtilities;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import java.util.UUID;

@Mixin(SolarArcanaSkill.class)
public class SolarArcanaSkillMixin {

    @Unique
    private static final UUID EVENT_UUID = UUID.fromString("b6b0ee46-56b3-4008-9fba-d2594b1e2676");

    @Inject(method = "onInitiate", at = @At("HEAD"), remap = false) @SuppressWarnings("removal")
    private void injectOnInitiate(SkillContainer container, CallbackInfo ci) {
        var listener = container.getExecutor().getEventListener();

        listener.addEventListener(
                PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT,
                EVENT_UUID,
                (event) -> {
                    ServerPlayerPatch patch = event.getPlayerPatch();
                    var capability = patch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
                    var itemPlayer = patch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND).getItem();
                    var registryItem = ExtendedDatapacksUtilities.getSafeItem("wom", "solar");
                    if (!(itemPlayer.equals(registryItem)) && capability.getPassiveSkill() == WOMSkills.SOLAR_PASSIVE) {
                        container.getDataManager().setDataSync(WOMSkillDataKeys.CHARGE.get(), 4);
                    }
                }
        );
    }

    @Inject(method = "onRemoved", at = @At("HEAD"), remap = false)
    private void injectOnRemoved(@NotNull SkillContainer container, CallbackInfo ci) {
        container.getExecutor().getEventListener().removeListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
    }
}
