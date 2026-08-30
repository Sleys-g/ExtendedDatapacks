package sleys.efedp.system.innates.json.builder.wrapper.combo;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.helper.SkillTooltipHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class WPerComboInnateSkill extends WeaponInnateSkill {
    private final Map<AnimationManager.AnimationAccessor<? extends DynamicAnimation>, AnimationSkillValues> perComboSkillValues;
    protected List<JsonComponentArgs> tooltipComponents;
    protected boolean disableTooltipProperties;

    public static WPerComboInnateSkill.Builder createPerComboBuilder() {
        return new WPerComboInnateSkill.Builder(WPerComboInnateSkill::new)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE);
    }

    public WPerComboInnateSkill(WPerComboInnateSkill.Builder builder) {
        super(builder);
        this.perComboSkillValues = builder.perComboSkillValues;
        this.tooltipComponents = builder.tooltipComponents;
        this.disableTooltipProperties = builder.disableTooltipProperties;
    }

    public boolean checkExecuteCondition(SkillContainer container) {
        var playerPatch = container.getExecutor();
        var playerAnimator = playerPatch.getAnimator().getPlayerFor(null);
        if (playerAnimator == null) return false;
        var playerState = playerPatch.getEntityState();

        return this.perComboSkillValues.containsKey(playerAnimator.getAnimation().get().getAccessor()) &&
                playerState.inaction() &&
                playerState.canUseSkill();
    }

    public void executeOnServer(SkillContainer container, CompoundTag arguments) {
        var playerPatch = container.getExecutor();
        var animator = playerPatch.getServerAnimator().animationPlayer;
        var combo = this.perComboSkillValues.get(animator.getAnimation().get().getAccessor());
        if (combo == null) return;

        playerPatch.playAnimationSynchronized(
                combo.animationAccessor().get().getRealAnimation(),
                0.0F
        );

        super.executeOnServer(container, arguments);
    }

    @OnlyIn(Dist.CLIENT) @Override
    public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        if (tooltipComponents.isEmpty()) {
            List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
            if (this.disableTooltipProperties) return list;

            this.applyPhaseProperties(list, itemStack, cap, playerCap);
            return list;
        }

        List<Component> list = Lists.newArrayList();
        var translatableText = this.getTranslationKey();

        list.add(Component.translatable(translatableText)
                .append(Component.literal(String.format(" [%.0f]", this.consumption))
                        .withStyle(ChatFormatting.AQUA))
        );

        list.add(JsonComponentArgs.getFormattedAdditionalTooltip(
                this.tooltipComponents, translatableText + ".tooltip", itemStack)
        );


        /// Fix bypass No-Attack Anim
        if (disableTooltipProperties) return list; /// Delegate to JsonComponentArgs
        this.applyPhaseProperties(list, itemStack, cap, playerCap);
        return list;
    }

    private void applyPhaseProperties(List<Component> list, ItemStack itemStack,
                                      CapabilityItem cap, PlayerPatch<?> playerCap) {
        this.perComboSkillValues.forEach((targetAccessor, skillValues) ->  {
                    var animationAccessor = skillValues.animationAccessor();
                    if (animationAccessor.get() instanceof AttackAnimation attackAnimation) {
                        AttackAnimation.Phase[] phases = attackAnimation.phases;
                        var properties = skillValues.properties();
                        var phaseLength = phases.length;
                        var propertiesSize = properties.size();
                        for (int i = 0; i < Math.min(phaseLength, propertiesSize); i++) {
                            this.generateTooltipforPhase(
                                    list, itemStack, cap, playerCap, properties.get(i),
                                    SkillTooltipHelper.intToOrdinalString(i, phaseLength - 1)
                            );
                        }
                    }
                }
        );
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        this.perComboSkillValues.forEach((targetAccessor, skillValues) ->
                ExecutionTasks.operateAndGetResult(
                        ExecutionPolicy.RESIST,
                        skillValues.animationAccessor(), this::registryAnimationsData
                ).ifFailure(e ->
                        ExtendedDatapacks.LOGGER.error(
                                "[Per Combo - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                                this.registryName.getPath(), this.registryName.getNamespace()
                        )
                )
        );
        return this;
    }

    @ErrorHandled
    private AnimationManager.AnimationAccessor<? extends DynamicAnimation> registryAnimationsData(
            AnimationManager.AnimationAccessor<? extends DynamicAnimation> animationAccessor) {

        if (!(animationAccessor.get() instanceof AttackAnimation attack)) {
            ExtendedDatapacks.LOGGER.warn("[Per Combo - Innate Skill] The animation: {}, It is NOT an attack animation or one that inherits from it; it will proceed, however, the attempt to apply properties is suppressed....", animationAccessor);
            return null;
        }

        AttackAnimation.Phase[] phases = attack.phases;
        for (int i = 0; i < Math.min(phases.length, this.properties.size()); i++) {
            phases[i].addProperties(this.properties.get(i).entrySet());
        }
        return animationAccessor;
    }

    public static final class Builder extends WeaponInnateSkill.Builder<WPerComboInnateSkill.Builder> {
        private final Map<AnimationManager.AnimationAccessor<? extends DynamicAnimation>, AnimationSkillValues> perComboSkillValues = new HashMap<>();
        private List<JsonComponentArgs> tooltipComponents;
        private boolean disableTooltipProperties;

        public Builder(Function<WPerComboInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public void putPerComboAnimationData(AnimationManager.AnimationAccessor<? extends DynamicAnimation> targetAnimation, AnimationSkillValues animationData) {
            this.perComboSkillValues.put(targetAnimation, animationData);
        }

        public WPerComboInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public WPerComboInnateSkill.Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }
    }
}
