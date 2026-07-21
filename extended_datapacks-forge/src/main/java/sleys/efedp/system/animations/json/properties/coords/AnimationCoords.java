package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record AnimationCoords<T extends ActionAnimation>(
        MoveAnimationType coordType,
        AnimationMoveCoordFunctions coordsFunctions
) implements IAnimationCoord<T> {

    public static <T extends ActionAnimation> MapCodec<AnimationCoords<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        MoveAnimationType.CODEC.fieldOf("coord_type")
                                        .forGetter(AnimationCoords::coordType),
                        AnimationMoveCoordFunctions.CODEC.fieldOf("coord_function")
                                .forGetter(AnimationCoords::coordsFunctions)
                ).apply(instance, AnimationCoords::new)
        );
    }

    @Override
    public void applyCoords(T animation) {
        switch (coordType) {
            case COORD_SET_BEGIN, COORD_SET_TICK -> animation.addProperty(
                    AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN,
                    coordsFunctions.setter
            );
            case COORD_GET -> animation.addProperty(
                    AnimationProperty.ActionAnimationProperty.COORD_GET,
                    coordsFunctions.getter
            );
            case DEST_LOCATION_PROVIDER -> animation.addProperty(
                    AnimationProperty.ActionAnimationProperty.DEST_LOCATION_PROVIDER,
                    coordsFunctions.destLocationProvider
            );
            case ENTITY_YROT_PROVIDER -> animation.addProperty(
                    AnimationProperty.ActionAnimationProperty.ENTITY_YROT_PROVIDER,
                    coordsFunctions.yRotProvider
            );
            case DEST_COORD_YROT_PROVIDER -> animation.addProperty(
                    AnimationProperty.ActionAnimationProperty.DEST_COORD_YROT_PROVIDER,
                    coordsFunctions.yRotProvider
            );
        }
    }
}
