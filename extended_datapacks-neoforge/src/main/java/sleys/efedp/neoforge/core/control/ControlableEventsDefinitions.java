package sleys.efedp.neoforge.core.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.core.control.controlable.ControlableSkillEvents;

/// Example of a request to read an event handler
public record ControlableEventsDefinitions(ControlableSkillEvents controlableEvents) {

    public static final Codec<ControlableEventsDefinitions> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ControlableSkillEvents.CODEC.optionalFieldOf("controlable_events", ControlableSkillEvents.EMPTY)
                                    .forGetter(ControlableEventsDefinitions::controlableEvents)
                    ).apply(instance, ControlableEventsDefinitions::new)
            );
}
