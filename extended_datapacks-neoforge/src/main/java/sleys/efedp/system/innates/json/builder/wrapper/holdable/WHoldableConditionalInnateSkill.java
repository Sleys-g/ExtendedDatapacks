package sleys.efedp.system.innates.json.builder.wrapper.holdable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.helper.SkillTooltipHelper;
import sleys.efedp.system.innates.json.builder.values.AnimationSkillValues;
import sleys.efedp.system.innates.json.builder.values.HoldableSkillValues;
import sleys.efedp.system.innates.json.builder.values.ListenerSkillValues;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import sleys.sl.epicfight.skills.interfaces.movement.IOnMovementInputEFSkillEvent;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.side.ClientOnly;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.network.server.SPSkillFeedback;
import yesman.epicfight.registry.entries.EpicFightSynchedAnimationVariableKeys;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.modules.ChargeableSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class WHoldableConditionalInnateSkill extends WeaponInnateSkill implements ChargeableSkill, IOnMovementInputEFSkillEvent {
    protected Map<ConditionalType, AnimationSkillValues> conditionMap;
    protected AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation;
    protected List<JsonComponentArgs> tooltipComponents;
    protected ListenerSkillValues listenerValues;
    protected HoldableSkillValues holdableValues;
    protected boolean disableTooltipProperties;

    public static WHoldableConditionalInnateSkill.Builder createHoldableConditionalInnateSkillBuilder(
            Function<WHoldableConditionalInnateSkill.Builder, ? extends WHoldableConditionalInnateSkill> constructor) {

        return new WHoldableConditionalInnateSkill.Builder(constructor)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE)
                .setActivateType(Skill.ActivateType.HELD);
    }

    public WHoldableConditionalInnateSkill(WHoldableConditionalInnateSkill.Builder builder) {
        super(builder);
        this.conditionMap = builder.conditionMap;
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
    public WeaponInnateSkill registerPropertiesToAnimation() {
        for (var entry : this.conditionMap.values()) {
            ExecutionTasks.operateAndGetResult(
                    ExecutionPolicy.RESIST,
                    entry, this::registryAnimationsData
            ).ifFailure(e ->
                    ExtendedDatapacks.LOGGER.error(
                            "[Holdable Conditional - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                            this.registryName.getPath(), this.registryName.getNamespace()
                    )
            );
        }
        return this;
    }

    @ErrorHandled
    private AnimationSkillValues registryAnimationsData(AnimationSkillValues entry) {
        var animation = entry.animationAccessor().get();
        if (!(animation instanceof AttackAnimation attack)) {
            ExtendedDatapacks.LOGGER.warn("[Holdable Conditional - Innate Skill] The animation: {}, It is NOT an attack animation or one that inherits from it; it will proceed, however, the attempt to apply properties is suppressed....", animation);
            return null;
        }

        for (int i = 0; i < Math.min(attack.phases.length, entry.properties().size()); i++) {
            attack.phases[i].addProperties(entry.properties().get(i).entrySet());
        }
        return entry;
    }

    @Override
    public void onStopHolding(SkillContainer container, SPSkillFeedback feedback) {
        this.playSkillAnimation(container.getServerExecutor());
        this.cancelOnServer(container, null);
    }

    protected void playSkillAnimation(ServerPlayerPatch executor) {
        var player = executor.getOriginal();
        for (var entry : this.conditionMap.entrySet()) {
            var condition = entry.getKey().predicate;
            var values = entry.getValue();

            if (condition.test(player) && values != null) {
                var animations = values.animationAccessor().get().getRealAnimation();
                executor.getAnimator().getVariables().put(
                        EpicFightSynchedAnimationVariableKeys.CHARGING_TICKS.get(),
                        animations, executor.getAccumulatedChargeTicks()
                );

                executor.playAnimationSynchronized(animations, 0.0F);
                break;
            }
        }
    }

    @Override
    public void holdTick(SkillContainer container) {
        ChargeableSkill.super.holdTick(container);
    }

    @ClientOnly
    public KeyMapping getKeyMapping() {
        return EpicFightKeyMappings.WEAPON_INNATE_SKILL;
    }

    @OnlyIn(Dist.CLIENT) @Override
    public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        if (tooltipComponents.isEmpty()) {
            List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
            if (this.disableTooltipProperties) return list;

            this.applyPhaseProperties(list, itemStack, cap, playerCap);
            return list;
        }

        List<Component> list = new ArrayList<>();
        String translatableText = this.getTranslationKey();
        list.add(Component.translatable(translatableText)
                .append(Component.literal(String.format(" [%.0f]", this.consumption))
                        .withStyle(ChatFormatting.AQUA))
        );
        list.add(JsonComponentArgs.getFormattedAdditionalTooltip(tooltipComponents, translatableText + ".tooltip", itemStack));

        if (this.disableTooltipProperties) return list;
        this.applyPhaseProperties(list, itemStack, cap, playerCap);
        return list;
    }

    private void applyPhaseProperties(List<Component> list, ItemStack itemStack,
                                      CapabilityItem cap, PlayerPatch<?> playerCap) {

        this.conditionMap.forEach((conditions, values) -> {
            if (values.animationAccessor().get() instanceof AttackAnimation attackAnimation) {
                AttackAnimation.Phase[] phases = attackAnimation.phases;
                var properties = values.properties();
                var phaseLength = phases.length;
                var propertiesSize = properties.size();
                for (int i = 0; i < Math.min(phaseLength, propertiesSize); i++) {
                    this.generateTooltipforPhase(
                            list, itemStack, cap, playerCap, properties.get(i),
                            SkillTooltipHelper.intToOrdinalString(i, phaseLength - 1, conditions)
                    );
                }
            }
        });
    }

    public static final class Builder extends WeaponInnateSkill.Builder<WHoldableConditionalInnateSkill.Builder> {
        private final Map<ConditionalType, AnimationSkillValues> conditionMap = new EnumMap<>(ConditionalType.class);
        private AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation;
        private List<JsonComponentArgs> tooltipComponents;
        private ListenerSkillValues listenerValues;
        private HoldableSkillValues holdableValues;
        private boolean disableTooltipProperties;

        public Builder(Function<WHoldableConditionalInnateSkill.Builder, ? extends WHoldableConditionalInnateSkill> constructor) {
            super(constructor);
        }

        public void putConditionData(ConditionalType type, AnimationSkillValues data) {
            this.conditionMap.put(type, data);
        }

        public WHoldableConditionalInnateSkill.Builder setChargingAnimation(AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation) {
            this.chargingAnimation = chargingAnimation;
            return this;
        }

        public WHoldableConditionalInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public WHoldableConditionalInnateSkill.Builder setListenerValues(ListenerSkillValues listenerValues) {
            this.listenerValues = listenerValues;
            return this;
        }

        public WHoldableConditionalInnateSkill.Builder setHoldableValues(HoldableSkillValues holdableValues) {
            this.holdableValues = holdableValues;
            return this;
        }

        public WHoldableConditionalInnateSkill.Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }
    }
}
