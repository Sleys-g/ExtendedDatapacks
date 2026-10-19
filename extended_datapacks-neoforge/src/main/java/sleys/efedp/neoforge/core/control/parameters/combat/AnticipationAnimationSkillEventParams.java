package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.events.EFOnTargetEvent;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import yesman.epicfight.skill.SkillContainer;

public record AnticipationAnimationSkillEventParams() implements ISkillEventParams<EFPlayerAnimationEvent.AnimationEvent> {

    public static final MapCodec<AnticipationAnimationSkillEventParams> CODEC = MapCodec.unit(AnticipationAnimationSkillEventParams::new);

    @Override
    public void applyEventParams(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {}
}