package sleys.efedp.neoforge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.controlable.EventControl;
import sleys.efedp.neoforge.core.control.controlable.IEventController;
import sleys.efedp.neoforge.core.control.list.combat.*;
import sleys.efedp.neoforge.core.control.registry.IControlableEventType;

public enum CombatControlableEvents implements IControlableEventType {
    ANTICIPATION_ANIMATION_EVENT(EventControl.codec(AnticipationAnimationEventController.CODEC)),
    ATTACK_ANIMATION_EVENT(EventControl.codec(AttackAnimationEventController.CODEC)),
    RECOVERY_ANIMATION_EVENT(EventControl.codec(RecoveryAnimationEventController.CODEC)),
    ANIMATION_PHASE_EVENT(EventControl.codec(AnimationPhaseEventController.CODEC)),

    ATTACK_ENTITY_EVENT(EventControl.codec(AttackEntityEventController.CODEC)),
    ATTACK_SPEED_EVENT(EventControl.codec(AttackSpeedEventController.CODEC)),

    DEAL_PRE_PLAYER_DAMAGE_EVENT(EventControl.codec(DealPrePlayerDamageEventController.CODEC)),
    DEAL_POST_PLAYER_DAMAGE_EVENT(EventControl.codec(DealPostPlayerDamageEventController.CODEC)),
    DEAL_PRE_DAMAGE_EVENT(EventControl.codec(DealPreDamageEventController.CODEC)),
    DEAL_POST_DAMAGE_EVENT(EventControl.codec(DealPostDamageEventController.CODEC)),
    TAKE_PRE_DAMAGE_EVENT(EventControl.codec(TakePreDamageEventController.CODEC)),
    TAKE_POST_DAMAGE_EVENT(EventControl.codec(TakePostDamageEventController.CODEC)),

    LIVING_DEATH_EVENT(EventControl.codec(LivingDeathEventController.CODEC)),
    LIVING_KILL_EVENT(EventControl.codec(LivingKillEventController.CODEC)),

    LIVING_INCOMING_PLAYER_DEALT_DAMAGE_EVENT(EventControl.codec(LivingIncomingPlayerDealDamageEventController.CODEC)),
    LIVING_INCOMING_DEALT_DAMAGE_EVENT(EventControl.codec(LivingIncomingDealDamageEventController.CODEC)),
    LIVING_INCOMING_TAKE_DAMAGE_EVENT(EventControl.codec(LivingIncomingTakeDamageEventController.CODEC)),

    LIVING_KNOCKBACK_EVENT(EventControl.codec(LivingKnockbackEventController.CODEC)),

    LIVING_SHIELD_BLOCKED_EVENT(EventControl.codec(LivingShieldBlockedEventController.CODEC)),
    LIVING_SHIELD_BLOCK_EVENT(EventControl.codec(LivingShieldBlockEventController.CODEC)),

    LIVING_USE_TOTEM_EVENT(EventControl.codec(LivingUseTotemEventController.CODEC)),
    LIVING_TRIGGER_TOTEM_EVENT(EventControl.codec(LivingTriggerTotemEventController.CODEC));

    private final MapCodec<? extends IEventController<? extends Event>> codec;

    CombatControlableEvents(MapCodec<? extends IEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}