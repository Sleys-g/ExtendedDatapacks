package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record CoordGetter<T extends ActionAnimation>(MoveCoordGetterFn function) implements IAnimationCoord<T> {

    public static <T extends ActionAnimation> MapCodec<CoordGetter<T>> codec() {
        return MoveCoordGetterFn.CODEC.fieldOf("coord_function").xmap(CoordGetter::new, CoordGetter::function);
    }

    @Override
    public AnimationCoordType coordType() {
        return AnimationCoordType.COORD_GET;
    }

    @Override
    public void applyCoords(T animation) {
        animation.addProperty(AnimationProperty.ActionAnimationProperty.COORD_GET, function.getter);
    }
}
