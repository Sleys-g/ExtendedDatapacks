package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import yesman.epicfight.api.animation.types.ActionAnimation;

public sealed interface IAnimationCoord<T extends ActionAnimation> permits
        CoordBeginSetter, CoordGetter, CoordTickSetter,
        DestCoordYRotProvider, DestLocationProvider, EntityYRotProvider {

    static <T extends ActionAnimation> Codec<IAnimationCoord<T>> codec() {
        return AnimationCoordType.CODEC.dispatch(
                "coord_type",
                IAnimationCoord::coordType,
                type -> switch (type) {
                    case COORD_SET_BEGIN -> CoordBeginSetter.codec();
                    case COORD_SET_TICK -> CoordTickSetter.codec();
                    case COORD_GET -> CoordGetter.codec();
                    case DEST_LOCATION_PROVIDER -> DestLocationProvider.codec();
                    case ENTITY_YROT_PROVIDER -> EntityYRotProvider.codec();
                    case DEST_COORD_YROT_PROVIDER -> DestCoordYRotProvider.codec();
                }
        );
    }

    AnimationCoordType coordType();

    void applyCoords(T animation);
}
