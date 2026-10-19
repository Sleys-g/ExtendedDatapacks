package sleys.efedp.neoforge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.GenericSkillEventControl;
import sleys.efedp.neoforge.core.control.ISkillEventController;
import sleys.efedp.neoforge.core.control.parameters.combat.*;
import sleys.efedp.neoforge.core.control.registry.IControlableSkillEventType;

public enum CombatControlableSkillEvents implements IControlableSkillEventType {
    ANTICIPATION_ANIMATION_EVENT(GenericSkillEventControl.codec(AnticipationAnimationSkillEventParams.CODEC)),
    ATTACK_ANIMATION_EVENT(GenericSkillEventControl.codec(AttackAnimationSkillEventParams.CODEC)),
    RECOVERY_ANIMATION_EVENT(GenericSkillEventControl.codec(RecoveryAnimationSkillEventParams.CODEC)),
    ANIMATION_PHASE_EVENT(GenericSkillEventControl.codec(AnimationPhaseSkillEventParams.CODEC)),

    ATTACK_ENTITY_EVENT(GenericSkillEventControl.codec(AttackEntitySkillEventParams.CODEC)),
    ATTACK_SPEED_EVENT(GenericSkillEventControl.codec(AttackSpeedSkillEventParams.CODEC)),

    DEAL_PRE_PLAYER_DAMAGE_EVENT(GenericSkillEventControl.codec(DealPrePlayerDamageSkillEventParams.CODEC)),
    DEAL_POST_PLAYER_DAMAGE_EVENT(GenericSkillEventControl.codec(DealPostPlayerDamageSkillEventParams.CODEC)),
    DEAL_PRE_DAMAGE_EVENT(GenericSkillEventControl.codec(DealPreDamageSkillEventParams.CODEC)),
    DEAL_POST_DAMAGE_EVENT(GenericSkillEventControl.codec(DealPostDamageSkillEventParams.CODEC)),
    TAKE_PRE_DAMAGE_EVENT(GenericSkillEventControl.codec(TakePreDamageSkillEventParams.CODEC)),
    TAKE_POST_DAMAGE_EVENT(GenericSkillEventControl.codec(TakePostDamageSkillEventParams.CODEC)),

    LIVING_DEATH_EVENT(GenericSkillEventControl.codec(LivingDeathSkillEventParams.CODEC)),
    LIVING_KILL_EVENT(GenericSkillEventControl.codec(LivingKillSkillEventParams.CODEC)),

    LIVING_INCOMING_PLAYER_DEALT_DAMAGE_EVENT(GenericSkillEventControl.codec(LivingIncomingPlayerDealDamageSkillEventParams.CODEC)),
    LIVING_INCOMING_DEALT_DAMAGE_EVENT(GenericSkillEventControl.codec(LivingIncomingDealDamageSkillEventParams.CODEC)),
    LIVING_INCOMING_TAKE_DAMAGE_EVENT(GenericSkillEventControl.codec(LivingIncomingTakeDamageSkillEventParams.CODEC)),

    LIVING_KNOCKBACK_EVENT(GenericSkillEventControl.codec(LivingKnockbackSkillEventParams.CODEC)),

    LIVING_SHIELD_BLOCKED_EVENT(GenericSkillEventControl.codec(LivingShieldBlockedSkillEventParams.CODEC)),
    LIVING_SHIELD_BLOCK_EVENT(GenericSkillEventControl.codec(LivingShieldBlockSkillEventParams.CODEC)),

    LIVING_USE_TOTEM_EVENT(GenericSkillEventControl.codec(LivingUseTotemEventParams.CODEC)),
    LIVING_TRIGGER_TOTEM_EVENT(GenericSkillEventControl.codec(LivingTriggerTotemEventParams.CODEC));

    private final MapCodec<? extends ISkillEventController<? extends Event>> codec;

    CombatControlableSkillEvents(MapCodec<? extends ISkillEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends ISkillEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}