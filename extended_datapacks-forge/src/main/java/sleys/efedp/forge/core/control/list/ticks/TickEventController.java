package sleys.efedp.forge.core.control.list.ticks;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

public record TickEventController() implements IControlableEventParams<Event> {

    public static final MapCodec<TickEventController> CODEC = MapCodec.unit(TickEventController::new);

    @Override
    public void applyEventParams(Event event, SkillContainer container) {}
}
