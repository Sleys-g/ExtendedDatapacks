package sleys.efedp.system.animations.json.properties.functional.time;

import com.mojang.serialization.*;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.AnimationEventSideCodec;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.IAnimationEventParams;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.Locale;
import java.util.stream.Stream;

public record AnimationsOnProcessEvent<T extends StaticAnimation>(ProcessType processType,
                                                                  IAnimationEventType type,
                                                                  AnimationEvent.Side side,
                                                                  IAnimationEventParams params) implements IAnimationEvent<T> {

    private enum ProcessType {
        ON_BEGIN(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS),
        ON_TICKS(AnimationProperty.StaticAnimationProperty.TICK_EVENTS),
        ON_END(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS);

        final AnimationProperty.StaticAnimationProperty<?> eventType;

        ProcessType(AnimationProperty.StaticAnimationProperty<?> eventType) {
            this.eventType = eventType;
        }

        public static final Codec<ProcessType> CODEC = EnumCodecs.byId(
                values(), e -> e.name().toUpperCase(Locale.ROOT)
        );

        public <A extends StaticAnimation> void applyProcessEvent(A animation, AnimationsOnProcessEvent<A> processEvent) {
            var event = AnimationEvent.SimpleEvent.create(
                    (livingEntityPatch,
                     assetAccessor,
                     animationParameters) ->
                            processEvent.type.runEvent(processEvent.params, assetAccessor, livingEntityPatch), processEvent.side
            );
            animation.addEvents(this.eventType, event);
        }
    }

    public static <T extends StaticAnimation> Codec<AnimationsOnProcessEvent<T>> codec() {
        return new MapCodec<AnimationsOnProcessEvent<T>>() {

            @Override
            public <O> Stream<O> keys(DynamicOps<O> ops) {
                return Stream.of(
                        ops.createString("process"),
                        ops.createString("type"),
                        ops.createString("side"),
                        ops.createString("params")
                );
            }

            @Override
            public <O> DataResult<AnimationsOnProcessEvent<T>> decode(DynamicOps<O> ops, MapLike<O> input) {
                O processTypeRaw = input.get("process");
                if (processTypeRaw == null) {
                    return DataResult.error(() -> "Missing 'process' field in animation event");
                }

                return ProcessType.CODEC.parse(ops, processTypeRaw).flatMap(processType -> {

                    O typeRaw = input.get("type");
                    if (typeRaw == null) {
                        ExtendedDatapacks.LOGGER.error(
                                "Failed to decode (On) {} Event: missing 'type' field", processType
                        );
                        return DataResult.<AnimationsOnProcessEvent<T>>error(() -> "Missing 'type' field in animation event");
                    }

                    return AnimationEventTypeRegistry.CODEC.parse(ops, typeRaw).flatMap(type -> {
                        O sideRaw = input.get("side");
                        O paramsRaw = input.get("params");

                        DataResult<AnimationEvent.Side> sideResult = sideRaw != null
                                ? AnimationEventSideCodec.CODEC.parse(ops, sideRaw)
                                : DataResult.error(() -> "Missing 'side' field in animation event");

                        DataResult<? extends IAnimationEventParams> paramsResult = type
                                .paramsCodec()
                                .codec()
                                .parse(ops, paramsRaw != null ? paramsRaw : ops.emptyMap());

                        paramsResult.error().ifPresent(error ->
                                ExtendedDatapacks.LOGGER.error(
                                        "Failed to decode (On) {} Event Lambda, Type: {}: Error: {}",
                                        processType, type, error.message()
                                )
                        );

                        return sideResult.flatMap(side ->
                                paramsResult.map(params ->
                                        new AnimationsOnProcessEvent<>(processType, type, side, params)
                                )
                        );
                    });
                }).ifError(error ->
                        ExtendedDatapacks.LOGGER.error(
                                "Failed to decode (On) Process Event: {}", error.message()
                        )
                );
            }

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <O> RecordBuilder<O> encode(AnimationsOnProcessEvent<T> input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
                prefix.add("process", ProcessType.CODEC.encodeStart(ops, input.processType()));
                prefix.add("type", AnimationEventTypeRegistry.CODEC.encodeStart(ops, input.type()));
                prefix.add("side", AnimationEventSideCodec.CODEC.encodeStart(ops, input.side()));

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
        if (side == null || type == null || processType == null) {
            ExtendedDatapacks.LOGGER.error("[Animation On Begin Event] Some of your statements are not valid, looking... Side: {}, Type: {}, Process Type: {}",
                    this.isValid(side), this.isValid(type), this.isValid(processType)
            );
            return;
        }

        processType().applyProcessEvent(animation, this);
    }
}
