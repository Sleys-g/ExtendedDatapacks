package sleys.efedp.neoforge.core.control.list.movement;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.sl.epicfight.events.EFKeyUseInputEvent;
import yesman.epicfight.skill.SkillContainer;

public record PlayerClientUseInputEventController() implements IControlableEventParams<EFKeyUseInputEvent.ClientUseInputEvent> {

    public static final MapCodec<PlayerClientUseInputEventController> CODEC = MapCodec.unit(PlayerClientUseInputEventController::new);

    @Override
    public void applyEventParams(EFKeyUseInputEvent.ClientUseInputEvent event, SkillContainer container) {}
}