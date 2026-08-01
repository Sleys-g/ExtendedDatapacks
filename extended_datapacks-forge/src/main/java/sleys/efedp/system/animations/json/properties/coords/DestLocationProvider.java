package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record DestLocationProvider<T extends ActionAnimation>(DestLocationProviderFn function) implements IAnimationCoord<T> {

    public static <T extends ActionAnimation> MapCodec<DestLocationProvider<T>> codec() {
        return DestLocationProviderFn.CODEC.fieldOf("coord_function").xmap(DestLocationProvider::new, DestLocationProvider::function);
    }

    @Override
    public AnimationCoordType coordType() {
        return AnimationCoordType.DEST_LOCATION_PROVIDER;
    }

    @Override
    public void applyCoords(T animation) {
        animation.addProperty(AnimationProperty.ActionAnimationProperty.DEST_LOCATION_PROVIDER, function.destLocationProvider);
    }
}
