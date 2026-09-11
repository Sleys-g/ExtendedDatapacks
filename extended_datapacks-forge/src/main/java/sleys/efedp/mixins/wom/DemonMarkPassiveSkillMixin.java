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
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
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
    )
    private Item redirectItemCheck(ItemStack stack, SkillContainer container) {
        return ExecutionTasks.getRaw(
                ExecutionPolicy.RESIST,
                () -> this.extended_datapacks$searchParity(stack, container)
        ).fold(item -> item, exception -> stack.getItem());
    }

    @Unique @ErrorHandled @SuppressWarnings("removal")
    private Item extended_datapacks$searchParity(ItemStack stack, SkillContainer container) {
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
    }

    @ModifyConstant(
            method = "updateContainer",
            constant = @Constant(intValue = 7),
            remap = false
    )
    private int modifyParticleCount(int original, SkillContainer container) {
        return ExecutionTasks.getRaw(
                ExecutionPolicy.RESIST,
                () -> this.extended_datapacks$searchParticleParity(original, container)
        ).fold(
                item -> item,
                exception -> original
        );
    }

    @Unique @ErrorHandled
    private int extended_datapacks$searchParticleParity(int original, SkillContainer container) {
        if (container == null || container.getExecutor() == null) return original;
        var entityPatch = container.getExecutor();

        var player = entityPatch.getOriginal();
        if (player == null) return original;

        var targetItem = ExtendedDatapacksUtilities.getSafeItem("wom", "antitheus");
        var actualItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();

        if (targetItem == null || actualItem == targetItem) return original;
        boolean isAllowDemonParticles = WoMSkillAccessorBuilder
                .getSafeEntryAsDemonMark(InteractionHand.MAIN_HAND, player)
                .map(WoMSkillAccessorBuilder.DemonMarkPassiveHelper::allowAntitheusParticles)
                .orElse(false);

        return isAllowDemonParticles ? original : 0;
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
        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                () -> extended_datapacks$listenSkillDataKeys(manager, key, container),
                manager.getDataValue(key)
        );
    }

    @Unique @ErrorHandled
    private Object extended_datapacks$listenSkillDataKeys(SkillDataManager manager, SkillDataKey<?> key, SkillContainer container) {
        if (!key.equals(WOMSkillDataKeys.BASIC_ATTACK.get())) return manager.getDataValue(key);
        var originalValue = manager.getDataValue(key);

        if (container.getExecutor() == null) return originalValue;
        var entityPatch = container.getExecutor();

        var player = entityPatch.getOriginal();
        if (player == null) return originalValue;

        var targetItem = ExtendedDatapacksUtilities.getSafeItem("wom", "antitheus");
        var actualItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();

        if (targetItem == null || actualItem == targetItem) return originalValue;

        boolean allowBasicParticles = WoMSkillAccessorBuilder
                .getSafeEntryAsDemonMark(InteractionHand.MAIN_HAND, player)
                .map(WoMSkillAccessorBuilder.DemonMarkPassiveHelper::allowBasicAntitheusParticles)
                .orElse(false);

        return allowBasicParticles ? originalValue : Boolean.FALSE;
    }
}