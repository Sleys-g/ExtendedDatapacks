package sleys.efedp.system.combat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import sleys.efedp.registry.ExtendedDatapacksRegistrySkills;
import sleys.sl.epicfight.util.helper.patch.PatchPlayerHelper;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class MechanicsAssignerEvent {

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent.Post event) {
        PlayerPatch<?> playerPatch = PatchPlayerHelper.safeParametricePlayerPatchTickEvent(event);
        if (playerPatch != null) {
            ExecutionTasks.operateAndGetResult(
                    ExecutionPolicy.RESIST,
                    playerPatch, MechanicsAssignerEvent::onChargedSlot
            );
        }
    }

    private static PlayerPatch<?> onChargedSlot(PlayerPatch<?> playerPatch) {
        var chargedSlot = playerPatch.getSkill(ExtendedSkillSlot.CHARGED_ATTACK);
        var chargedSkill = ExtendedDatapacksRegistrySkills.CHARGED_ATTACK;
        var chargedSlotSkill = chargedSlot.getSkill();
        if (chargedSlotSkill == null && !chargedSlot.hasSkill(chargedSkill.get())) chargedSlot.setSkill(chargedSkill.get());
        return playerPatch;
    }
}
