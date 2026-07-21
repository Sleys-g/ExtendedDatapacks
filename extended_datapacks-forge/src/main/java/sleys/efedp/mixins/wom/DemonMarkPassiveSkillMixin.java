package sleys.efedp.mixins.wom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import reascer.wom.gameasset.WOMSkills;
import reascer.wom.skill.WOMSkillDataKeys;
import reascer.wom.skill.weaponpassive.DemonMarkPassiveSkill;
import sleys.efedp.capability.ExtendedDatapacksUtilities;
import sleys.efedp.system.thirdparty.wom.json.WoMSkillAccessorBuilder;
import sleys.sl.library.runtime.policy.PolicyRuntimeTasks;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;

@Mixin(DemonMarkPassiveSkill.class)
public class DemonMarkPassiveSkillMixin {

    @Redirect(
            method = "updateContainer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"
            )
    ) @SuppressWarnings("removal")
    private Item redirectItemCheck(ItemStack stack, SkillContainer container) {
        try {
            Item originalItem = stack.getItem();
            if (originalItem == ExtendedDatapacksUtilities.getSafeItem("wom", "antitheus")) {
                return originalItem;
            }

            var executor = container.getExecutor();
            var capability = executor.getHoldingItemCapability(InteractionHand.MAIN_HAND);
            if (capability != null && capability.getPassiveSkill() == WOMSkills.DEMON_MARK_PASSIVE) {
                return ExtendedDatapacksUtilities.getSafeItem("wom", "antitheus");
            }

            return originalItem;
        } catch (Exception ignored) {
            return stack.getItem();
        }
    }

    @ModifyConstant(
            method = "updateContainer",
            constant = @Constant(intValue = 7),
            remap = false
    )
    private int modifyParticleCount(int original, SkillContainer container) {
        try {
            if (container != null && container.getExecutor() != null) {
                var entityPatch = container.getExecutor();
                var player = entityPatch.getOriginal();
                if (player != null) {
                    var targetItem = ExtendedDatapacksUtilities.getSafeItem("wom", "antitheus");
                    var actualItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();

                    if (targetItem != null && actualItem != targetItem) {
                        boolean isAllowDemonParticles = WoMSkillAccessorBuilder.getSafeEntryAsDemonMark(InteractionHand.MAIN_HAND, player)
                                .map(WoMSkillAccessorBuilder.DemonMarkPassiveHelper::allowAntitheusParticles)
                                .orElse(false);

                        if (isAllowDemonParticles) {
                            return original;
                        }
                        return 0;
                    }
                }
            }
            return original;
        } catch (Exception ignored) {
            return original;
        }
    }

    @Redirect(
            method = "updateContainer",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/skill/SkillDataManager;getDataValue(Lyesman/epicfight/skill/SkillDataKey;)Ljava/lang/Object;"
            ),
            remap = false
    )
    private Object redirectAntitheusIf(SkillDataManager manager, SkillDataKey<?> key, SkillContainer container) {
        return PolicyRuntimeTasks.getOrDefault(
                ErrorPolicy.DEPURATE_ERROR,
                () -> extended_datapacks$listenSkillDataKeys(manager, key, container),
                manager.getDataValue(key)
        );
    }

    @Unique
    private Object extended_datapacks$listenSkillDataKeys(SkillDataManager manager, SkillDataKey<?> key, SkillContainer container) {
        if (key.equals(WOMSkillDataKeys.BASIC_ATTACK.get())) {
            var originalValue = manager.getDataValue(key);
            if (container.getExecutor() != null) {
                var entityPatch = container.getExecutor();
                var player = entityPatch.getOriginal();
                if (player != null) {
                    var targetItem = ExtendedDatapacksUtilities.getSafeItem("wom", "antitheus");
                    var actualItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();

                    if (targetItem != null && actualItem != targetItem) {
                        boolean allowBasicParticles = WoMSkillAccessorBuilder.getSafeEntryAsDemonMark(InteractionHand.MAIN_HAND, player)
                                .map(WoMSkillAccessorBuilder.DemonMarkPassiveHelper::allowBasicAntitheusParticles)
                                .orElse(false);

                        return allowBasicParticles ? originalValue : Boolean.FALSE;
                    }
                }
            }
            return originalValue;
        }
        return manager.getDataValue(key);
    }
}