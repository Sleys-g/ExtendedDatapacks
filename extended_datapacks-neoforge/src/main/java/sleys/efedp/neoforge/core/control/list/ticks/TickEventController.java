package sleys.efedp.neoforge.core.control.list.ticks;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

public record TickEventController() implements IControlableEventParams<Event> {

    public static final MapCodec<TickEventController> CODEC = MapCodec.unit(TickEventController::new);

    @Override
    public void applyEventParams(Event event, SkillContainer container) {}
}
