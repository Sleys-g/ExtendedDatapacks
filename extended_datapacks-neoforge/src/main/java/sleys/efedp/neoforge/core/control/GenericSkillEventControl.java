package sleys.efedp.neoforge.core.control;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.operations.EventControlOperation;
import sleys.efedp.neoforge.core.control.operations.EventInvocationOperation;

public record GenericSkillEventControl<E extends Event>(EventControlOperation<E> eventControl,
                                                        EventInvocationOperation eventsOperation) implements ISkillEventController<E> {

    public static <E extends Event> MapCodec<GenericSkillEventControl<E>> codec(MapCodec<? extends ISkillEventParams<E>> paramsCodec) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        EventControlOperation.codec(paramsCodec)
                                .fieldOf("event_control")
                                .forGetter(GenericSkillEventControl::eventControl),
                        EventInvocationOperation.CODEC
                                .fieldOf("events_operation")
                                .forGetter(GenericSkillEventControl::eventsOperation)
                ).apply(instance, GenericSkillEventControl::new)
        );
    }
}
