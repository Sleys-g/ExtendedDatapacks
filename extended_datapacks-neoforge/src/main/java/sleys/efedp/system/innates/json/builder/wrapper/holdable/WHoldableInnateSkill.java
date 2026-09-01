package sleys.efedp.system.innates.json.builder.wrapper.holdable;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.values.HoldableSkillValues;
import sleys.efedp.system.innates.json.builder.values.ListenerSkillValues;
import sleys.efedp.system.innates.json.builder.helper.SkillTooltipHelper;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import sleys.sl.epicfight.skills.interfaces.movement.IOnMovementInputEFSkillEvent;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.side.ClientOnly;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.server.SPSkillFeedback;
import yesman.epicfight.registry.entries.EpicFightSynchedAnimationVariableKeys;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.modules.ChargeableSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.List;
import java.util.function.Function;

public class WHoldableInnateSkill extends WeaponInnateSkill implements ChargeableSkill, IOnMovementInputEFSkillEvent  {
    protected AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation;
    protected AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
    protected List<JsonComponentArgs> tooltipComponents;
    protected ListenerSkillValues listenerValues;
    protected HoldableSkillValues holdableValues;
    protected boolean disableTooltipProperties;

    public static WHoldableInnateSkill.Builder createHoldableInnateSkillBuilder(
            Function<WHoldableInnateSkill.Builder, ? extends WHoldableInnateSkill> constructor) {

        return new WHoldableInnateSkill.Builder(constructor)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE)
                .setActivateType(Skill.ActivateType.HELD);
    }

    public WHoldableInnateSkill(WHoldableInnateSkill.Builder builder) {
        super(builder);
        this.animation = builder.animation;
        this.chargingAnimation = builder.chargingAnimation;
        this.tooltipComponents = builder.tooltipComponents;
        this.listenerValues = builder.listenerValues;
        this.holdableValues = builder.holdableValues;
        this.disableTooltipProperties = builder.disableTooltipProperties;
    }

    @Override
    public String putCaller() {
        return listenerValues.caller();
    }

    @Override
    public ResourceLocation putSkill() {
        return listenerValues.skill();
    }

    @Override
    public int getAllowedMaxChargingTicks() {
        return holdableValues.MaxAllowedMaxChargingTicks();
    }

    @Override
    public int getMaxChargingTicks() {
        return holdableValues.MaxChargingTicks();
    }

    @Override
    public int getMinChargingTicks() {
        return holdableValues.MinChargingTicks();
    }

    @Override @OnlyIn(Dist.CLIENT)
    public void onMovementInputEvent(EFMovementInputEvent.InputEvent movementInput, SkillContainer container) {
        if (container.getExecutor().isHoldingSkill(this) && holdableValues.reduceSpeed()) {
            movementInput.getPlayer().setSprinting(false);
            ((net.minecraft.client.player.LocalPlayer) movementInput.getPlayer()).sprintTriggerTime = -1;
            ControlEngine.setSprintingKeyStateNotDown();
            var input = movementInput.getInput();

            if (container.getExecutor() != null && input != null) {
                float chargeProgress = container.getExecutor().getSkillChargingTicks() / 30.0F;
                float slowFactor = 1.0F - 0.8F * chargeProgress;
                input.forwardImpulse *= slowFactor;
                input.leftImpulse *= slowFactor;

                if (slowFactor < 0.5F) {
                    input.leftImpulse *= 0.7F;
                }
            }
        }
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        ExecutionTasks.runAndGetResult(
                ExecutionPolicy.RESIST,
                this::registryAnimationsData
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.error(
                "[Holdable - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                this.registryName.getPath(), this.registryName.getNamespace()
        ));
        return this;
    }

    @ErrorHandled
    private void registryAnimationsData() {
        /// Add Speed Modifier in CT
        if (holdableValues.playbackForCharging()) {
            chargingAnimation.get().addProperty(
                    AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                    Animations.ReusableSources.CHARGING
            );
        }

        if (!(this.animation.get() instanceof AttackAnimation attackAnimation)) return;
        AttackAnimation.Phase[] phases = attackAnimation.phases;
        for (int i = 0; i < Math.min(phases.length, this.properties.size()); i++) {
            phases[i].addProperties(this.properties.get(i).entrySet());
        }

        /// Add Speed Modifier (Attack) in CT
        if (holdableValues.playbackForRelease()) {
            attackAnimation.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                    (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                        if (elapsedTime < 1.05F) {
                            int chargingPower = entitypatch
                                    .getAnimator()
                                    .getVariables()
                                    .get(EpicFightSynchedAnimationVariableKeys.CHARGING_TICKS.get(), self.getRealAnimation()).orElse(0);

                            return 0.6666F + (float)chargingPower / 20.0F;
                        } else {
                            return 1.0F;
                        }
                    });
        }
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
        String translatableText = this.getTranslationKey();

        list.add(Component.translatable(translatableText)
                .append(Component.literal(String.format(" [%.0f]", this.consumption))
                        .withStyle(ChatFormatting.AQUA))
        );

        list.add(JsonComponentArgs.getFormattedAdditionalTooltip(
                this.tooltipComponents, translatableText + ".tooltip", itemStack)
        );

        if (disableTooltipProperties) return list; /// Delegate to JsonComponentArgs
        this.applyPhaseProperties(list, itemStack, cap, playerCap);
        return list;
    }

    private void applyPhaseProperties(List<Component> list, ItemStack itemStack,
                                      CapabilityItem cap, PlayerPatch<?> playerCap) {
        if (this.animation.get() instanceof AttackAnimation attackAnimation) {
            AttackAnimation.Phase[] phases = attackAnimation.phases;
            var phaseLength = phases.length;
            var propertiesSize = this.properties.size();
            for (int i = 0; i < Math.min(phaseLength, propertiesSize); i++) {
                this.generateTooltipforPhase(
                        list, itemStack, cap, playerCap, this.properties.get(i),
                        SkillTooltipHelper.intToOrdinalString(i, phaseLength - 1)
                );
            }
        }
    }

    @Override @SuppressWarnings("all")
    public void startHolding(SkillContainer container) {
        AssetAccessor<? extends StaticAnimation> currentPlaying = container
                .getExecutor()
                .getAnimator()
                .getPlayerFor(null)
                .getRealAnimation();

        if (currentPlaying.get().isMainFrameAnimation()) {
            container.getExecutor().stopPlaying(currentPlaying);
        }

        container.getExecutor().playAnimationSynchronized(this.chargingAnimation, 0.0F);
    }

    @Override
    public void resetHolding(SkillContainer container) {
        if (container.getExecutor().isLogicalClient()) {
            container.getExecutor().getAnimator().stopPlaying(this.chargingAnimation);
        } else {
            container.getExecutor().stopPlaying(this.chargingAnimation);
        }

    }

    @Override
    public void onStopHolding(SkillContainer container, SPSkillFeedback feedback) {
        container.getExecutor().getAnimator().getVariables().put(
                EpicFightSynchedAnimationVariableKeys.CHARGING_TICKS.get(),
                this.animation, container.getExecutor().getAccumulatedChargeTicks()
        );

        container.getExecutor().playAnimationSynchronized(this.animation, 0.0F);
        this.cancelOnServer(container, null);
    }

    @Override
    public void holdTick(SkillContainer container) {
        ChargeableSkill.super.holdTick(container);
    }

    @ClientOnly
    public KeyMapping getKeyMapping() {
        return EpicFightKeyMappings.WEAPON_INNATE_SKILL;
    }

    public static final class Builder extends WeaponInnateSkill.Builder<WHoldableInnateSkill.Builder> {
        private AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation;
        private AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
        private List<JsonComponentArgs> tooltipComponents;
        private ListenerSkillValues listenerValues;
        private HoldableSkillValues holdableValues;
        private boolean disableTooltipProperties;

        public Builder(Function<WHoldableInnateSkill.Builder, ? extends WHoldableInnateSkill> constructor) {
            super(constructor);
        }

        public WHoldableInnateSkill.Builder setAnimation(AnimationManager.AnimationAccessor<? extends StaticAnimation> animation) {
            this.animation = animation;
            return this;
        }

        public WHoldableInnateSkill.Builder setChargingAnimation(AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation) {
            this.chargingAnimation = chargingAnimation;
            return this;
        }

        public WHoldableInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public WHoldableInnateSkill.Builder setListenerValues(ListenerSkillValues listenerValues) {
            this.listenerValues = listenerValues;
            return this;
        }

        public WHoldableInnateSkill.Builder setHoldableValues(HoldableSkillValues holdableValues) {
            this.holdableValues = holdableValues;
            return this;
        }

        public WHoldableInnateSkill.Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }
    }
}
