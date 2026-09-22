package sleys.efedp.neoforge.system.animations.json.groups.config;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IAnimationConfig<T extends StaticAnimation> permits ConfigActionAnimationGroup, ConfigAttackAnimationGroup, ConfigStaticAnimationGroup, IAnimationExternalConfig {

    IAnimationGroupType groupType();
    ResourceLocation animation();

    default AnimationManager.AnimationAccessor<? extends T> getAccessor() {
        return AnimationManager.byKey(animation());
    }

    default boolean isValidAccessor() {
        if (this.getAccessor() == null) {
            var error = String.format("""
                    [<I> - Config Animation] The existing animation could not be configured or parameterized because its accessor is null...
                    Accessor: %s
                    Animation (ID): %s
                    Group: %s
                   """, this.getAccessor(), this.animation(), this.getGroupId()
            );
            ConfigAnimationsErrorPool.addError(error);
            return false;
        }
        return true;
    }

    default boolean isInvalidAccessor() {
        return !isValidAccessor();
    }

    void applyConfig(IAnimationProperties<T> property);

    default void isSuccessful() {
        ExtendedDatapacks.LOGGER.info(
                "[<I> - Config Animation] Successfully processed animation config: {}",
                this.getAccessor()
        );
    }

    default String getGroupId() {
        return this.groupType().toString();
    }
}
