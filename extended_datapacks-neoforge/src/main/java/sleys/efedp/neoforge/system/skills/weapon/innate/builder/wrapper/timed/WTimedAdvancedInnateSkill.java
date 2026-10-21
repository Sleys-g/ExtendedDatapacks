package sleys.efedp.neoforge.system.skills.weapon.innate.builder.wrapper.timed;


import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.core.control.controlable.ControlableEvents;
import sleys.efedp.neoforge.core.control.types.*;
import sleys.efedp.neoforge.system.skills.weapon.innate.builder.helper.SkillTooltipHelper;
import sleys.efedp.neoforge.system.skills.weapon.innate.builder.values.ListenerSkillValues;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import sleys.sl.epicfight.events.EFKeyUseInputEvent;
import sleys.sl.epicfight.events.EFOnTargetEvent;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import sleys.sl.epicfight.events.EFPlayerAttackSpeedEvent;
import sleys.sl.epicfight.skills.interfaces.combat.*;
import sleys.sl.epicfight.skills.interfaces.ia.IOnTargetEntityEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.loot.IOnLivingDropsEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.loot.IOnLivingExperienceDropEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.movement.IOnLivingBreathEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.movement.IOnLivingFallEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.movement.IOnMovementInputEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.movement.IOnPlayerUseInputEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.status.IOnLivingHealEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.status.IOnMobEffectEFSkillEvent;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class WTimedAdvancedInnateSkill extends WeaponInnateSkill implements
        /// Combat
        IOnTargetEntityEFSkillEvent, IOnAnimationPhaseEFSkillEvent, IOnAttackEntityEFSkillEvent,
        IOnAttackSpeedEFSkillEvent, IOnLivingDamageEFSkillEvent, IOnLivingDeathEFSkillEvent,
        IOnLivingIncomingDamageEFSkillEvent, IOnLivingKnockBackEFSkillEvent,
        IOnLivingShieldBlockEFSkillEvent, IOnLivingUseTotemEFSkillEvent,

        /// Status
        IOnLivingDropsEFSkillEvent, IOnLivingExperienceDropEFSkillEvent,
        IOnLivingBreathEFSkillEvent, IOnLivingFallEFSkillEvent, IOnMovementInputEFSkillEvent,
        IOnPlayerUseInputEFSkillEvent, IOnLivingHealEFSkillEvent, IOnMobEffectEFSkillEvent {

    protected final AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
    protected final List<JsonComponentArgs> tooltipComponents;
    protected final boolean disableTooltipProperties;
    protected final Optional<SoundEvent> soundEvent;
    protected final ListenerSkillValues listenerSkillValues;
    protected final ControlableEvents controlableEvents;

    public static WTimedAdvancedInnateSkill.Builder createTimedBuilder(
            Function<WTimedAdvancedInnateSkill.Builder, ? extends WTimedAdvancedInnateSkill> constructor) {

        return new WTimedAdvancedInnateSkill.Builder(constructor)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE)
                .setActivateType(ActivateType.DURATION);
    }

    public WTimedAdvancedInnateSkill(WTimedAdvancedInnateSkill.Builder builder) {
        super(builder);
        this.animation = builder.animation;
        this.tooltipComponents = builder.tooltipComponents;
        this.disableTooltipProperties = builder.disableTooltipProperties;
        this.soundEvent = builder.soundEvent;
        this.listenerSkillValues = builder.listenerSkillValues;
        this.controlableEvents = builder.controlableEvents;
    }

    @Override
    public void loadDatapackParameters(CompoundTag parameters) {
        super.loadDatapackParameters(parameters);
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag arguments) {
        soundEvent.ifPresent(sound -> container.getExecutor().playSound(sound, 0.0F, 0.0F));
        if (this.isActivated(container)) this.cancelOnServer(container, arguments);
        else {
            super.executeOnServer(container, arguments);
            container.activate();
            container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
            container.getExecutor().playAnimationSynchronized(animation, 0.0F);
        }
    }

    @Override
    public void cancelOnServer(SkillContainer container, CompoundTag arguments) {
        container.deactivate();
        super.cancelOnServer(container, arguments);
        container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
    }

    @Override
    public void executeOnClient(SkillContainer container, CompoundTag arguments) {
        super.executeOnClient(container, arguments);
        container.activate();
    }

    @Override
    public void cancelOnClient(SkillContainer container, CompoundTag arguments) {
        super.cancelOnClient(container, arguments);
        container.deactivate();
    }

    @Override
    public boolean canExecute(SkillContainer container) {
        if (container.getExecutor().isLogicalClient()) return super.canExecute(container);
        else {
            var hand = container.getExecutor().getPrimaryHand();
            var itemstack = container.getExecutor().getOriginal().getItemInHand(hand);
            return EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(container.getExecutor(), itemstack) == this
                    && container.getExecutor().getOriginal().getVehicle() == null;
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

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        ExecutionTasks.runAndGetResult(
                ExecutionPolicy.RESIST,
                this::registryAnimationsData
        ).ifFailure(e ->
                ExtendedDatapacks.LOGGER.error(
                        "[Timed - Innate Skill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                        this.registryName.getPath(), this.registryName.getNamespace()
                )
        );
        return this;
    }

    private void registryAnimationsData() {
        if (!(this.animation.get() instanceof AttackAnimation attackAnimation)) return;
        AttackAnimation.Phase[] phases = attackAnimation.phases;
        for (int i = 0; i < Math.min(phases.length, this.properties.size()); i++) {
            phases[i].addProperties(this.properties.get(i).entrySet());
        }
    }

    @Override
    public String putCaller() {
        return listenerSkillValues.caller();
    }

    @Override
    public ResourceLocation putSkill() {
        return listenerSkillValues.skill();
    }

    @Override
    public void onLivingBreathEvent(LivingBreatheEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                MovementControlableEvents.LIVING_BREATH_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingFallEvent(LivingFallEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                MovementControlableEvents.LIVING_FALL_EVENT,
                event, container
        );
    }

    @Override @OnlyIn(Dist.CLIENT)
    public void onMovementInputEvent(EFMovementInputEvent.InputEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                MovementControlableEvents.MOVEMENT_INPUT_EVENT,
                event, container
        );
    }

    @Override @OnlyIn(Dist.CLIENT)
    public void onPlayerClientUseInputEvent(EFKeyUseInputEvent.ClientUseInputEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                MovementControlableEvents.PLAYER_CLIENT_USE_INPUT_EVENT,
                event, container
        );
    }

    @Override
    public void onPlayerServerUseInputEvent(EFKeyUseInputEvent.ServerUseInputEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                MovementControlableEvents.PLAYER_SERVER_USE_INPUT_EVENT,
                event, container
        );
    }

    @Override
    public void onPlayerUseInputEvent(EFKeyUseInputEvent.UseInputEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                MovementControlableEvents.PLAYER_USE_INPUT_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingHealEvent(LivingHealEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                StatusControlableEvents.LIVING_HEAL_EVENT,
                event, container
        );
    }

    @Override
    public void onAddedMobEffect(MobEffectEvent.Added event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                StatusControlableEvents.ADDED_MOB_EFFECT_EVENT,
                event, container
        );
    }

    @Override
    public void onRemoveMobEffect(MobEffectEvent.Remove event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                StatusControlableEvents.REMOVE_MOB_EFFECT_EVENT,
                event, container
        );
    }

    @Override
    public void onExpiredMobEffect(MobEffectEvent.Expired event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                StatusControlableEvents.EXPIRED_MOB_EFFECT_EVENT,
                event, container
        );
    }

    @Override
    public void onApplicableMobEffect(MobEffectEvent.Applicable event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                StatusControlableEvents.APPLICABLE_MOB_EFFECT_EVENT,
                event, container
        );
    }


    @Override
    public void onAnticipationAnimationEvent(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.ANTICIPATION_ANIMATION_EVENT,
                event, container
        );
    }

    @Override
    public void onAttackAnimationEvent(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.ATTACK_ANIMATION_EVENT,
                event, container
        );
    }

    @Override
    public void onRecoveryAnimationEvent(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.RECOVERY_ANIMATION_EVENT,
                event, container
        );
    }

    @Override
    public void onAnimationPhaseEvent(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.ANIMATION_PHASE_EVENT,
                event, container
        );
    }

    @Override
    public void onAttackEntity(AttackEntityEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.ATTACK_ENTITY_EVENT,
                event, container
        );
    }

    @Override
    public void onAttackSpeed(EFPlayerAttackSpeedEvent.EFAttackSpeedEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.ATTACK_SPEED_EVENT,
                event, container
        );
    }

    @Override
    public void onDealPrePlayerDamageEvent(LivingDamageEvent.Pre event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.DEAL_PRE_PLAYER_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onDealPostPlayerDamageEvent(LivingDamageEvent.Post event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.DEAL_POST_PLAYER_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onDealPreDamageEvent(LivingDamageEvent.Pre event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.DEAL_PRE_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onTakePreDamageEvent(LivingDamageEvent.Pre event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.TAKE_PRE_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onDealPostDamageEvent(LivingDamageEvent.Post event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.DEAL_POST_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onTakePostDamageEvent(LivingDamageEvent.Post event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.TAKE_POST_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingKillEvent(LivingDeathEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_KILL_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingDeathEvent(LivingDeathEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_DEATH_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingIncomingPlayerDealtDamageEvent(LivingIncomingDamageEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_INCOMING_PLAYER_DEALT_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingIncomingDealtDamageEvent(LivingIncomingDamageEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_INCOMING_DEALT_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingIncomingTakeDamageEvent(LivingIncomingDamageEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_INCOMING_TAKE_DAMAGE_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingKnockBackEvent(LivingKnockBackEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_KNOCKBACK_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingShieldBlockedEvent(LivingShieldBlockEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_SHIELD_BLOCKED_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingShieldBlockEvent(LivingShieldBlockEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_SHIELD_BLOCK_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingUseTotemEvent(LivingUseTotemEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_USE_TOTEM_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingTriggerTotemEvent(LivingUseTotemEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                CombatControlableEvents.LIVING_TRIGGER_TOTEM_EVENT,
                event, container
        );
    }

    @Override
    public void onTargetEntity(EFOnTargetEvent.EpicFightTargetEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                IAControlableEvents.TARGET_ENTITY_EVENT,
                event, container
        );
    }

    @Override
    public void onServerTargetEntity(EFOnTargetEvent.EpicFightServerTargetEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                IAControlableEvents.SERVER_TARGET_ENTITY_EVENT,
                event, container
        );
    }

    @Override
    public void onClientTargetEntity(EFOnTargetEvent.EpicFightClientTargetEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                IAControlableEvents.CLIENT_TARGET_ENTITY_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingDropsEvent(LivingDropsEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                LootControlableEvents.LIVING_DROPS_EVENT,
                event, container
        );
    }

    @Override
    public void onLivingExperienceDropEvent(LivingExperienceDropEvent event, SkillContainer container) {
        if (!this.isActivated(container)) return;
        controlableEvents.run(
                LootControlableEvents.LIVING_EXPERIENCE_DROP_EVENT,
                event, container
        );
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if (!this.isActivated(container)) return;

        controlableEvents.run(
                TickControlableEvents.TICK_EVENT,
                null, container
        );

        if (container.getExecutor().isLogicalClient()) {
            controlableEvents.run(
                    TickControlableEvents.CLIENT_TICK_EVENT,
                    null, container
            );
        } else {
            controlableEvents.run(
                    TickControlableEvents.SERVER_TICK_EVENT,
                    null, container
            );
        }
    }

    public static final class Builder extends WeaponInnateSkill.Builder<WTimedAdvancedInnateSkill.Builder> {
        private AnimationManager.AnimationAccessor<? extends StaticAnimation> animation;
        private List<JsonComponentArgs> tooltipComponents;
        private boolean disableTooltipProperties;
        private Optional<SoundEvent> soundEvent;
        private ListenerSkillValues listenerSkillValues;
        private ControlableEvents controlableEvents;

        public Builder(Function<WTimedAdvancedInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public WTimedAdvancedInnateSkill.Builder setActivationSound(Optional<SoundEvent> soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        public WTimedAdvancedInnateSkill.Builder setAnimations(AnimationManager.AnimationAccessor<? extends StaticAnimation> animation) {
            this.animation = animation;
            return this;
        }

        public WTimedAdvancedInnateSkill.Builder setTooltipArray(List<JsonComponentArgs> tooltipComponents) {
            this.tooltipComponents = tooltipComponents;
            return this;
        }

        public WTimedAdvancedInnateSkill.Builder setDisableTooltipProperties(boolean disableTooltipProperties) {
            this.disableTooltipProperties = disableTooltipProperties;
            return this;
        }

        public WTimedAdvancedInnateSkill.Builder setListenerSkillValues(ListenerSkillValues listenerSkillValues) {
            this.listenerSkillValues = listenerSkillValues;
            return this;
        }

        public Builder setControlableEvents(ControlableEvents controlableEvents) {
            this.controlableEvents = Objects.requireNonNull(controlableEvents);
            return this;
        }
    }
}
