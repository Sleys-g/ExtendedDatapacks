package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import sleys.sl.epicfight.events.EFPlayerAttackSpeedEvent;
import yesman.epicfight.skill.SkillContainer;

public record AttackSpeedSkillEventParams() implements ISkillEventParams<EFPlayerAttackSpeedEvent.EFAttackSpeedEvent> {

    public static final MapCodec<AttackSpeedSkillEventParams> CODEC = MapCodec.unit(AttackSpeedSkillEventParams::new);

    @Override
    public void applyEventParams(EFPlayerAttackSpeedEvent.EFAttackSpeedEvent event, SkillContainer container) {}
}