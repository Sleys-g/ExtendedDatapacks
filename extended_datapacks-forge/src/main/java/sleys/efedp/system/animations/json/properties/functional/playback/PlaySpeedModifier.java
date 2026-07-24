package sleys.efedp.system.animations.json.properties.functional.playback;

import com.mojang.serialization.*;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.playback.lambda.IPlaySpeedModifierParams;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.stream.Stream;

public record PlaySpeedModifier<T extends StaticAnimation>(PlaySpeedModifierLambdaList type,
                                                           IPlaySpeedModifierParams params) implements IPlaySpeedModifier<T> {

    public static <T extends StaticAnimation> Codec<PlaySpeedModifier<T>> codec() {
        return new MapCodec<PlaySpeedModifier<T>>() {

            @Override
            public <O> Stream<O> keys(DynamicOps<O> ops) {
                return Stream.of(
                        ops.createString("type"),
                        ops.createString("params")
                );
            }

            @Override
            public <O> DataResult<PlaySpeedModifier<T>> decode(DynamicOps<O> ops, MapLike<O> input) {
                O typeRaw = input.get("type");
                if (typeRaw == null) {
                    return DataResult.error(() -> "Missing 'type' field in Play Speed Animation Lambda");
                }

                return PlaySpeedModifierLambdaList.CODEC.parse(ops, typeRaw).flatMap(type -> {
                    O paramsRaw = input.get("params");

                    DataResult<? extends IPlaySpeedModifierParams> paramsResult = type
                            .paramsCodec()
                            .codec()
                            .parse(ops, paramsRaw != null ? paramsRaw : ops.emptyMap());

                    paramsResult.error().ifPresent(error ->
                            ExtendedDatapacks.LOGGER.error(
                                    "Failed to decode Play Speed Modifier Lambda, Type: {}:  Error: {}",
                                    type,
                                    error.message()
                            )
                    );

                    return  paramsResult.map(params ->
                            new PlaySpeedModifier<>(type, params)
                    );
                });
            }

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <O> RecordBuilder<O> encode(PlaySpeedModifier<T> input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
                prefix.add("type", PlaySpeedModifierLambdaList.CODEC.encodeStart(ops, input.type()));
                if (input.params() != null) {
                    Codec<IPlaySpeedModifierParams> paramsCodec = (Codec) input.type().paramsCodec().codec();
                    prefix.add("params", paramsCodec.encodeStart(ops, input.params()));
                }
                return prefix;
            }
        }.codec();
    }

    @Override
    public void applySpeedModifier(T animation) {
        animation.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,
                ((dynamicAnimation, livingEntityPatch, speed, prevElapse, elapseTime) ->
                        type.applyModifiers(params, dynamicAnimation, livingEntityPatch, speed, prevElapse, elapseTime)
                )
        );
    }
}
