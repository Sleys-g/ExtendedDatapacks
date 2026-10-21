package sleys.efedp.neoforge.core.control.controlable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.control.operations.EventControlOperation;
import sleys.efedp.neoforge.core.control.operations.EventInvocationOperation;
import sleys.sl.library.annotations.Internal;

import java.util.Optional;

@Internal
public record EventControl<E extends Event>(Optional<EventControlOperation<E>> eventControl,
                                            Optional<EventInvocationOperation> eventsOperation) implements IEventController<E> {

    public static <E extends Event> MapCodec<EventControl<E>> codec(MapCodec<? extends IControlableEventParams<E>> paramsCodec) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        EventControlOperation.codec(paramsCodec).codec()
                                .optionalFieldOf("event_control")
                                .forGetter(EventControl::eventControl),

                        EventInvocationOperation.CODEC.codec()
                                .optionalFieldOf("events_invocations")
                                .forGetter(EventControl::eventsOperation)
                ).apply(instance, EventControl::new)
        );
    }
}