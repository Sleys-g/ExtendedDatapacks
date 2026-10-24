package sleys.efedp.forge.core.control.list.ticks;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

public record ClientTickEventController() implements IControlableEventParams<Event> {

    public static final MapCodec<ClientTickEventController> CODEC = MapCodec.unit(ClientTickEventController::new);

    @Override
    public void applyEventParams(Event event, SkillContainer container) {}
}
