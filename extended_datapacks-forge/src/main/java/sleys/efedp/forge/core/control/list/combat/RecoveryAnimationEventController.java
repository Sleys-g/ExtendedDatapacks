package sleys.efedp.forge.core.control.list.combat;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import yesman.epicfight.skill.SkillContainer;

public record RecoveryAnimationEventController() implements IControlableEventParams<EFPlayerAnimationEvent.AnimationEvent> {

    public static final MapCodec<RecoveryAnimationEventController> CODEC = MapCodec.unit(RecoveryAnimationEventController::new);

    @Override
    public void applyEventParams(EFPlayerAnimationEvent.AnimationEvent event, SkillContainer container) {}
}