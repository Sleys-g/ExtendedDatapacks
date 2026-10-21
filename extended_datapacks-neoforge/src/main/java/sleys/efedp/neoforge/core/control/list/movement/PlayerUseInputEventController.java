package sleys.efedp.neoforge.core.control.list.movement;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.sl.epicfight.events.EFKeyUseInputEvent;
import yesman.epicfight.skill.SkillContainer;

public record PlayerUseInputEventController() implements IControlableEventParams<EFKeyUseInputEvent.UseInputEvent> {

    public static final MapCodec<PlayerUseInputEventController> CODEC = MapCodec.unit(PlayerUseInputEventController::new);

    @Override
    public void applyEventParams(EFKeyUseInputEvent.UseInputEvent event, SkillContainer container) {}
}