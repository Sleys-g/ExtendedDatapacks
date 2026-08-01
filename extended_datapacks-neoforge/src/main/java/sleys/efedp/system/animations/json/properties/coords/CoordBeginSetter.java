package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record CoordBeginSetter<T extends ActionAnimation>(MoveCoordSetterFn function) implements IAnimationCoord<T> {

    public static <T extends ActionAnimation> MapCodec<CoordBeginSetter<T>> codec() {
        return MoveCoordSetterFn.CODEC.fieldOf("coord_function").xmap(CoordBeginSetter::new, CoordBeginSetter::function);
    }

    @Override
    public AnimationCoordType coordType() {
        return AnimationCoordType.COORD_SET_BEGIN;
    }

    @Override
    public void applyCoords(T animation) {
        animation.addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, function.setter);
    }
}