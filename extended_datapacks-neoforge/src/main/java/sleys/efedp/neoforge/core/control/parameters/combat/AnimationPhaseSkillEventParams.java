package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import yesman.epicfight.skill.SkillContainer;

public record AnimationPhaseSkillEventParams() implements ISkillEventParams<EFPlayerAnimationEvent.AnimationEvent> {

    public static final MapCodec<AnimationPhaseSkillEventParams> CODEC = MapCodec.unit(AnimationPhaseSkillEventParams::new);

    @Override
    public void applyEventParams(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {}
}