package sleys.efedp.mixins.wom;

import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.skill.weaponpassive.SolarPassiveSkill;
import sleys.efedp.capability.ExtendedDatapacksUtilities;
import sleys.efedp.system.thirdparty.wom.json.WoMSkillAccessorBuilder;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;


@Mixin(SolarPassiveSkill.class)
public class SolarPassiveSkillMixin {

    @Redirect(
            method = "updateContainer",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/skill/SkillDataManager;getDataValue(Lyesman/epicfight/skill/SkillDataKey;)Ljava/lang/Object;"
            ),
            remap = false
    )
    private Object redirectSolarObscuridad(SkillDataManager manager, SkillDataKey<?> key, SkillContainer container) {
        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                () -> extended_datapacks$listenSkillDataKeys(manager, key, container),
                manager.getDataValue(key)
        );
    }

    @Unique @ErrorHandled /// Fix NPE on Level
    private Object extended_datapacks$listenSkillDataKeys(SkillDataManager manager, SkillDataKey<?> key, SkillContainer container) {
        if (!key.equals(WOMSkillDataKeys.SOLAR_OBSCURIDAD.get())) return manager.getDataValue(key);

        var originalValue = manager.getDataValue(key);
        var entityPatch = container.getExecutor();
        if (entityPatch == null) return originalValue;
        if (!entityPatch.isLogicalClient()) return manager.getDataValue(key);

        var player = entityPatch.getOriginal();
        if (player == null) return originalValue;

        var targetItem = ExtendedDatapacksUtilities.getSafeItem("wom", "solar");
        var actualItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();

        if (targetItem == null || actualItem == targetItem) return originalValue;

        boolean allowSolarParticles = WoMSkillAccessorBuilder
                .getSafeEntryAsSolarPassive(InteractionHand.MAIN_HAND, player)
                .map(WoMSkillAccessorBuilder.SolarPassiveHelper::allowSolarParticles)
                .orElse(false);

        return allowSolarParticles ? originalValue : Boolean.FALSE;
    }
}
