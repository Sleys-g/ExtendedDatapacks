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
import reascer.wom.skill.weaponinnate.SakuraStateSkill;
import sleys.efedp.capability.ExtendedDatapacksUtilities;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

@Mixin(SakuraStateSkill.class)
public class SakuraStateSkillMixin {

    @Unique
    private static final UUID EVENT_UUID = UUID.fromString("2b67d169-416a-4206-ba3d-e7100d55d603");

    @Inject(method = "onInitiate", at = @At("TAIL"), remap = false) @SuppressWarnings("removal")
    private void injectCustomLogic(SkillContainer container, CallbackInfo ci) {
        var listener = container.getExecutor().getEventListener();
        listener.addEventListener(
                PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT,
                EVENT_UUID,
                (event) -> {
                    PlayerPatch<?> playerPatch = event.getPlayerPatch();
                    var capability = playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
                    var itemPlayer = playerPatch.getOriginal().getItemInHand(InteractionHand.MAIN_HAND).getItem();
                    var registryItem = ExtendedDatapacksUtilities.getSafeItem("wom", "satsujin");
                    if (registryItem != null && !(itemPlayer.equals(registryItem)) && capability.getPassiveSkill() == WOMSkills.SATSUJIN_PASSIVE) {
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
