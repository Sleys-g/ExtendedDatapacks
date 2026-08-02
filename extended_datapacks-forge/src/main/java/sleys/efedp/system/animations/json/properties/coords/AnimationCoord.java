package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.*;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

import java.util.stream.Stream;

public record AnimationCoord<T extends ActionAnimation>(AnimationCoordType coordType, ICoordFunction<?> function) {

    public static <T extends ActionAnimation> MapCodec<AnimationCoord<T>> codec() {
        return new MapCodec<>() {
            @Override
            public <O> Stream<O> keys(DynamicOps<O> ops) {
                return Stream.of(
                        ops.createString("coord_type"),
                        ops.createString("coord_function")
                );
            }

            @Override
            public <O> DataResult<AnimationCoord<T>> decode(DynamicOps<O> ops, MapLike<O> input) {
                O typeRaw = input.get("coord_type");
                if (typeRaw == null) {
                    return DataResult.error(() -> "Missing 'coord_type' field in Animation Coord Provider");
                }

                return AnimationCoordType.CODEC.parse(ops, typeRaw).flatMap(type -> {
                    O paramsRaw = input.get("coord_function");

                    DataResult<? extends ICoordFunction<?>> paramsResult = type
                            .functionCodec
                            .parse(ops, paramsRaw != null ? paramsRaw : ops.emptyMap());

                    paramsResult.error().ifPresent(error ->
                            ExtendedDatapacks.LOGGER.error(
                                    "Failed to decode Animation Coord, Type: {}:  Error: {}",
                                    type,
                                    error.message()
                            )
                    );

                    return  paramsResult.map(params ->
                            new AnimationCoord<>(type, params)
                    );
                });
            }

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public <A> RecordBuilder<A> encode(AnimationCoord<T> input,
                                               DynamicOps<A> ops,
                                               RecordBuilder<A> prefix) {
                prefix.add("coord_type", AnimationCoordType.CODEC.encodeStart(ops, input.coordType()));
                if (input.function() == null) throw new RegistryObjectException(
                        "When determining the use of a coordinate provider, it is necessary to implement a function that provides them, Since your function provider is null, the operation cannot continue"
                );

                Codec<ICoordFunction<?>> paramsCodec = (Codec) input.coordType().functionCodec;
                prefix.add("coord_function", paramsCodec.encodeStart(ops, input.function));
                return prefix;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public void applyCoords(T animation) {
        animation.addProperty(
                (AnimationProperty.ActionAnimationProperty<Object>) coordType.actionAnimationProperty,
                function.value()
        );
    }
}
