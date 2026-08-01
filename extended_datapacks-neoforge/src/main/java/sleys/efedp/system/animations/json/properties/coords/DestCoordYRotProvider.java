package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record DestCoordYRotProvider<T extends ActionAnimation>(YRotProviderFn function) implements IAnimationCoord<T> {

    public static <T extends ActionAnimation> MapCodec<DestCoordYRotProvider<T>> codec() {
        return YRotProviderFn.CODEC.fieldOf("coord_function").xmap(DestCoordYRotProvider::new, DestCoordYRotProvider::function);
    }

    @Override
    public AnimationCoordType coordType() {
        return AnimationCoordType.DEST_COORD_YROT_PROVIDER;
    }

    @Override
    public void applyCoords(T animation) {
        animation.addProperty(AnimationProperty.ActionAnimationProperty.DEST_COORD_YROT_PROVIDER, function.yRotProvider);
    }
}
