package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import yesman.epicfight.skill.SkillContainer;

public record RecoveryAnimationSkillEventParams() implements ISkillEventParams<EFPlayerAnimationEvent.AnimationEvent> {

    public static final MapCodec<RecoveryAnimationSkillEventParams> CODEC = MapCodec.unit(RecoveryAnimationSkillEventParams::new);

    @Override
    public void applyEventParams(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {}
}