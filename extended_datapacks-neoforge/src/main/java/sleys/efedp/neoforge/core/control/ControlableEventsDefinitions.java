package sleys.efedp.neoforge.core.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.core.control.controlable.ControlableEvents;

/// Example of a request to read an event handler
public record ControlableEventsDefinitions(ControlableEvents controlableEvents) {

    public static final Codec<ControlableEventsDefinitions> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ControlableEvents.CODEC.optionalFieldOf("controlable_events", ControlableEvents.EMPTY)
                                    .forGetter(ControlableEventsDefinitions::controlableEvents)
                    ).apply(instance, ControlableEventsDefinitions::new)
            );
}
