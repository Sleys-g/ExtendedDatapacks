package sleys.efedp.neoforge.system.combat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import sleys.efedp.neoforge.plataform.registry.ExtendedDatapacksSkills;
import sleys.sl.epicfight.util.helper.patch.PatchPlayerHelper;
import sleys.sl.library.annotations.ErrorHandled;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class MechanicsAssignerEvent {

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Post event) {
        PlayerPatch<?> playerPatch = PatchPlayerHelper.safeParametricePlayerPatchTickEvent(event);
        if (playerPatch == null) return;
        MechanicsAssignerEvent.onChargedSlot(playerPatch);
    }

    @ErrorHandled
    private static void onChargedSlot(PlayerPatch<?> playerPatch) {
        var chargedSlot = playerPatch.getSkill(ExtendedSkillSlot.CHARGED_ATTACK);
        var chargedSkill = ExtendedDatapacksSkills.CHARGED_ATTACK;
        var chargedSlotSkill = chargedSlot.getSkill();
        if (chargedSlotSkill == null && !chargedSlot.hasSkill(chargedSkill.get())) {
            chargedSlot.setSkill(chargedSkill.get());
        }
    }
}
