package sleys.efedp.neoforge.core.control.registry;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.controlable.IEventController;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.skill.SkillContainer;

@Internal
public interface IControlableEventType {
    MapCodec<? extends IEventController<? extends Event>> controllerCodec();

    default <T extends Event> void runEvent(IEventController<T> eventController,
                                            T event, SkillContainer container) {
        eventController.executeEventPayload(event, container);
    }
}
