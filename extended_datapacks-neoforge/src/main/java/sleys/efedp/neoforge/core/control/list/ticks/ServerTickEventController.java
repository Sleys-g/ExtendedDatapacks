package sleys.efedp.neoforge.core.control.list.ticks;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

public record ServerTickEventController() implements IControlableEventParams<Event> {

    public static final MapCodec<ServerTickEventController> CODEC = MapCodec.unit(ServerTickEventController::new);

    @Override
    public void applyEventParams(Event event, SkillContainer container) {}
}
