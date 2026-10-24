package sleys.efedp.forge.core.control.list.combat;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import yesman.epicfight.skill.SkillContainer;

public record AnticipationAnimationEventController() implements IControlableEventParams<EFPlayerAnimationEvent.AnimationEvent> {

    public static final MapCodec<AnticipationAnimationEventController> CODEC = MapCodec.unit(AnticipationAnimationEventController::new);

    @Override
    public void applyEventParams(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {}
}