package sleys.efedp.neoforge.system.animations.properties.time;

import com.mojang.serialization.*;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.core.ProcessSides;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.efedp.neoforge.core.events.registry.EventTypeRegistry;
import sleys.efedp.neoforge.core.events.registry.IEventType;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.stream.Stream;

public record AnimationsInTimeEvent<T extends StaticAnimation>(IEventType type,
                                                               ProcessSides side,
                                                               Float start,
                                                               IEventParams params) implements IAnimationEvent<T> {

    public static <T extends StaticAnimation> Codec<AnimationsInTimeEvent<T>> codec() {
        return new MapCodec<AnimationsInTimeEvent<T>>() {

            @Override
            public <O> Stream<O> keys(DynamicOps<O> ops) {
                return Stream.of(
                        ops.createString("type"),
                        ops.createString("side"),
                        ops.createString("start"),
                        ops.createString("params")
                );
            }

            @Override
            public <O> DataResult<AnimationsInTimeEvent<T>> decode(DynamicOps<O> ops, MapLike<O> input) {
                O typeRaw = input.get("type");
                if (typeRaw == null) {
                    return DataResult.error(() -> "Missing 'type' field in animation event");
                }

                return EventTypeRegistry.CODEC.parse(ops, typeRaw).flatMap(type -> {
                    O sideRaw = input.get("side");
                    O startRaw = input.get("start");
                    O paramsRaw = input.get("params");

                    DataResult<ProcessSides> sideResult = sideRaw != null
                            ? ProcessSides.PROCESS_SIDES_CODEC.parse(ops, sideRaw)
                            : DataResult.error(() -> "Missing 'side' field in animation event");

                    DataResult<Float> startResult = startRaw != null
                            ? Codec.FLOAT.parse(ops, startRaw)
                            : DataResult.error(() -> "Missing 'start' field in animation event");

                    DataResult<? extends IEventParams> paramsResult = type
                            .paramsCodec()
                            .codec()
                            .parse(ops, paramsRaw != null ? paramsRaw : ops.emptyMap());

                    paramsResult.error().ifPresent(error ->
                            ExtendedDatapacks.LOGGER.error(
                                    "Failed to decode (In) Time Event Lambda, Type: {}:  Error: {}",
                                    type,
                                    error.message()
                            )
                    );

                    return sideResult.flatMap(side ->
                            startResult.flatMap(start ->
                                    paramsResult.map(params ->
                                            new AnimationsInTimeEvent<>(type, side, start, params)
                                    )
                            )
                    );
                });
            }

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <O> RecordBuilder<O> encode(AnimationsInTimeEvent<T> input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
                prefix.add("type", EventTypeRegistry.CODEC.encodeStart(ops, input.type()));
                prefix.add("side", ProcessSides.PROCESS_SIDES_CODEC.encodeStart(ops, input.side()));
                prefix.add("start", Codec.FLOAT.encodeStart(ops, input.start()));

                if (input.params() != null) {
                    Codec<IEventParams> paramsCodec = (Codec) input.type().paramsCodec().codec();
                    prefix.add("params", paramsCodec.encodeStart(ops, input.params()));
                }
                return prefix;
            }
        }.codec();
    }

    @Override
    public void applyTo(T animation) {
        if (side == null || start == null || type == null) {
            ExtendedDatapacks.LOGGER.error("[Animation In Internal Time Event] Some of your statements are not valid, looking... Side: {}, start: {}, Type: {}",
                    this.isValid(side), this.isValid(start), this.isValid(type)
            );
            return;
        }

        var event = AnimationEvent.InTimeEvent.create(
                start, (livingEntityPatch,
                        accessor,
                        runtimeParams) ->
                        type.runEvent(params, accessor, livingEntityPatch), side.toEpicFightSides()
        );

        animation.addEvents(event);
    }
}
