package sleys.efedp.system.combat;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.registry.ExtendedDatapacksRegistrySkills;
import sleys.sl.epicfight.helper.patch.PatchPlayerHelper;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class MechanicsAssignerEvent {

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        PlayerPatch<?> playerPatch = PatchPlayerHelper.safeParametricePlayerPatchTickEvent(event);
        if (playerPatch != null) {
            onChargedSlot(playerPatch);
        }
    }

    private static void onChargedSlot(PlayerPatch<?> playerPatch) {
        var chargedSlot = playerPatch.getSkill(ExtendedSkillSlot.CHARGED_ATTACK);
        var chargedSkill = ExtendedDatapacksRegistrySkills.CHARGED_ATTACK;
        var chargedSlotSkill = chargedSlot.getSkill();
        if (chargedSlotSkill == null && chargedSkill != null && !chargedSlot.hasSkill(chargedSkill)) {
            chargedSlot.setSkill(chargedSkill);
        }
    }
}
