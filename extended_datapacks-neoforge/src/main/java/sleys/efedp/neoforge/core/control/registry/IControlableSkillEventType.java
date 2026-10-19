package sleys.efedp.neoforge.core.control.registry;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.ISkillEventController;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.skill.SkillContainer;

@Internal
public interface IControlableSkillEventType {
    MapCodec<? extends ISkillEventController<? extends Event>> controllerCodec();

    default <T extends Event> void runEvent(ISkillEventController<T> eventController,
                                            T event, SkillContainer container) {
        eventController.executeEventPayload(event, container);
    }
}
