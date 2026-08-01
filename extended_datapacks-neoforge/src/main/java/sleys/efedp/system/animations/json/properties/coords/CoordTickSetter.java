package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record CoordTickSetter<T extends ActionAnimation>(MoveCoordSetterFn function) implements IAnimationCoord<T> {

    public static <T extends ActionAnimation> MapCodec<? extends IAnimationCoord<?>> codec() {
        return MoveCoordSetterFn.CODEC.fieldOf("coord_function").xmap(CoordTickSetter::new, CoordTickSetter::function);
    }

    @Override
    public AnimationCoordType coordType() {
        return AnimationCoordType.COORD_SET_TICK;
    }

    @Override
    public void applyCoords(T animation) {
        animation.addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, function.setter);
    }
}