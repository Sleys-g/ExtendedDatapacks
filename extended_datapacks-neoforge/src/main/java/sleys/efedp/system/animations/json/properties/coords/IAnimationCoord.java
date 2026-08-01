package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.types.ActionAnimation;

public sealed interface IAnimationCoord<T extends ActionAnimation> permits
        CoordBeginSetter, CoordGetter, CoordTickSetter,
        DestCoordYRotProvider, DestLocationProvider, EntityYRotProvider {

    @SuppressWarnings("unchecked")
    static <T extends ActionAnimation> Codec<IAnimationCoord<T>> codec() {
        return AnimationCoordType.CODEC.dispatch(
                "coord_type",
                IAnimationCoord::coordType,
                type -> (MapCodec<? extends IAnimationCoord<T>>) type.animationCoordCodec
        );
    }

    AnimationCoordType coordType();

    void applyCoords(T animation);
}
