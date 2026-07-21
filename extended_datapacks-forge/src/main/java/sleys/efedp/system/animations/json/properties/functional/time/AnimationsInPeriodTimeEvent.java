package sleys.efedp.system.animations.json.properties.functional.time;

import com.mojang.serialization.*;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.AnimationEventSideCodec;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.IAnimationEventParams;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.stream.Stream;

public record AnimationsInPeriodTimeEvent<T extends StaticAnimation>(AnimationsEventsList type,
                                                                     AnimationEvent.Side side,
                                                                     Float start, Float end,
                                                                     IAnimationEventParams params) implements IAnimationEvent<T> {

    public static <T extends StaticAnimation> Codec<AnimationsInPeriodTimeEvent<T>> codec() {
        return new MapCodec<AnimationsInPeriodTimeEvent<T>>() {

            @Override
            public <O> Stream<O> keys(DynamicOps<O> ops) {
                return Stream.of(
                        ops.createString("type"),
                        ops.createString("side"),
                        ops.createString("start"),
                        ops.createString("end"),
                        ops.createString("params")
                );
            }

            @Override
            public <O> DataResult<AnimationsInPeriodTimeEvent<T>> decode(DynamicOps<O> ops, MapLike<O> input) {
                O typeRaw = input.get("type");
                if (typeRaw == null) {
                    return DataResult.error(() -> "Missing 'type' field in animation event");
                }

                return AnimationsEventsList.CODEC.parse(ops, typeRaw).flatMap(type -> {
                    O sideRaw = input.get("side");
                    O startRaw = input.get("start");
                    O endRaw = input.get("end");
                    O paramsRaw = input.get("params");

                    DataResult<AnimationEvent.Side> sideResult = sideRaw != null
                            ? AnimationEventSideCodec.CODEC.parse(ops, sideRaw)
                            : DataResult.error(() -> "Missing 'side' field in animation event");

                    DataResult<Float> startResult = startRaw != null
                            ? Codec.FLOAT.parse(ops, startRaw)
                            : DataResult.error(() -> "Missing 'start' field in animation event");

                    DataResult<Float> endResult = endRaw != null
                            ? Codec.FLOAT.parse(ops, endRaw)
                            : DataResult.error(() -> "Missing 'end' field in animation event");

                    DataResult<? extends IAnimationEventParams> paramsResult = type
                            .paramsCodec()
                            .codec()
                            .parse(ops, paramsRaw != null ? paramsRaw : ops.emptyMap());

                    paramsResult.error().ifPresent(error ->
                            ExtendedDatapacks.LOGGER.error(
                                    "Failed to decode (Period) Time Event Lambda, Type: {}:  Error: {}",
                                    type,
                                    error.message()
                            )
                    );

                    return sideResult.flatMap(side ->
                            startResult.flatMap(start -> endResult.flatMap( end ->
                                    paramsResult.map(params ->
                                            new AnimationsInPeriodTimeEvent<>(type, side, start, end, params)
                                    )
                            ))
                    );
                });
            }

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <O> RecordBuilder<O> encode(AnimationsInPeriodTimeEvent<T> input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
                prefix.add("type", AnimationsEventsList.CODEC.encodeStart(ops, input.type()));
                prefix.add("side", AnimationEventSideCodec.CODEC.encodeStart(ops, input.side()));
                prefix.add("start", Codec.FLOAT.encodeStart(ops, input.start()));
                prefix.add("end", Codec.FLOAT.encodeStart(ops, input.end()));

                if (input.params() != null) {
                    Codec<IAnimationEventParams> paramsCodec = (Codec) input.type().paramsCodec().codec();
                    prefix.add("params", paramsCodec.encodeStart(ops, input.params()));
                }
                return prefix;
            }
        }.codec();
    }

    @Override
    public void applyTo(T animation) {
        if (side == null || start == null || type == null) {
            ExtendedDatapacks.LOGGER.error("[Animation In Period Time Event] Some of your statements are not valid, looking... Side: {}, start: {}, End: {}, Type: {}",
                    this.isValid(side), this.isValid(start), this.isValid(end), this.isValid(type)
            );
            return;
        }

        var event = AnimationEvent.InPeriodEvent.create(
                start, end, (
                        livingEntityPatch,
                        accessor,
                        runtimeParams) ->
                        type.runEvent(params, accessor, livingEntityPatch), side
        );

        animation.addEvents(event);
    }
}
