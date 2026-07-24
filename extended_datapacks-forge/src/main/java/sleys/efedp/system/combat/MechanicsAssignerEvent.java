package sleys.efedp.system.combat;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.registry.ExtendedDatapacksRegistrySkills;
import sleys.sl.epicfight.util.helper.patch.PatchPlayerHelper;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class MechanicsAssignerEvent {

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        PlayerPatch<?> playerPatch = PatchPlayerHelper.safeParametricePlayerPatchTickEvent(event);
        if (playerPatch == null) return;
        ExecutionTasks.operateAndGetResult(
                ExecutionPolicy.RESIST,
                playerPatch, MechanicsAssignerEvent::onChargedSlot
        );
    }

    @ErrorHandled
    private static PlayerPatch<?> onChargedSlot(PlayerPatch<?> playerPatch) {
        var chargedSlot = playerPatch.getSkill(ExtendedSkillSlot.CHARGED_ATTACK);
        var chargedSkill = ExtendedDatapacksRegistrySkills.CHARGED_ATTACK;
        var chargedSlotSkill = chargedSlot.getSkill();
        if (chargedSlotSkill == null && chargedSkill != null && !chargedSlot.hasSkill(chargedSkill)) {
            chargedSlot.setSkill(chargedSkill);
        }
        return playerPatch;
    }
}
