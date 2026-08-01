package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record EntityYRotProvider<T extends ActionAnimation>(YRotProviderFn function) implements IAnimationCoord<T> {

    public static <T extends ActionAnimation> MapCodec<EntityYRotProvider<T>> codec() {
        return YRotProviderFn.CODEC.fieldOf("coord_function").xmap(EntityYRotProvider::new, EntityYRotProvider::function);
    }

    @Override
    public AnimationCoordType coordType() {
        return AnimationCoordType.ENTITY_YROT_PROVIDER;
    }

    @Override
    public void applyCoords(T animation) {
        animation.addProperty(AnimationProperty.ActionAnimationProperty.ENTITY_YROT_PROVIDER, function.yRotProvider);
    }
}
