package sleys.efedp.forge.core.control.list.movement;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.sl.epicfight.events.EFKeyUseInputEvent;
import yesman.epicfight.skill.SkillContainer;

public record PlayerServerUseInputEventController() implements IControlableEventParams<EFKeyUseInputEvent.ServerUseInputEvent> {

    public static final MapCodec<PlayerServerUseInputEventController> CODEC = MapCodec.unit(PlayerServerUseInputEventController::new);

    @Override
    public void applyEventParams(EFKeyUseInputEvent.ServerUseInputEvent event, SkillContainer container) {}
}