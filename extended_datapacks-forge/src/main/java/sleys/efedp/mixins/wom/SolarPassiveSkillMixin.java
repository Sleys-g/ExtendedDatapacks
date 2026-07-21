package sleys.efedp.mixins.wom;

import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.skill.weaponpassive.SolarPassiveSkill;
import sleys.efedp.capability.ExtendedDatapacksUtilities;
import sleys.efedp.system.thirdparty.wom.json.WoMSkillAccessorBuilder;
import sleys.sl.library.runtime.policy.PolicyRuntimeTasks;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;
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
        return PolicyRuntimeTasks.getOrDefault(
                ErrorPolicy.DEPURATE_ERROR,
                () -> extended_datapacks$listenSkillDataKeys(manager, key, container),
                manager.getDataValue(key)
        );
    }

    @Unique
    private Object extended_datapacks$listenSkillDataKeys(SkillDataManager manager, SkillDataKey<?> key, SkillContainer container) {
        if (key.equals(WOMSkillDataKeys.SOLAR_OBSCURIDAD.get())) {
            if (!container.getExecutor().isLogicalClient()) return manager.getDataValue(key);
            var originalValue = manager.getDataValue(key);
            if (container.getExecutor() != null) {
                var entityPatch = container.getExecutor();
                var player = entityPatch.getOriginal();
                if (player != null) {
                    var targetItem = ExtendedDatapacksUtilities.getSafeItem("wom", "solar");
                    var actualItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();

                    if (targetItem != null && actualItem != targetItem) {
                        boolean allowSolarParticles = WoMSkillAccessorBuilder.getSafeEntryAsSolarPassive(InteractionHand.MAIN_HAND, player)
                                .map(WoMSkillAccessorBuilder.SolarPassiveHelper::allowSolarParticles)
                                .orElse(false);

                        return allowSolarParticles ? originalValue : Boolean.FALSE;
                    }
                }
            }
            return originalValue;
        }
        return manager.getDataValue(key);
    }
}
