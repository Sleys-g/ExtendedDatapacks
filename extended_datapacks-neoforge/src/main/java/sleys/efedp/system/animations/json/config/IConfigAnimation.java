package sleys.efedp.system.animations.json.config;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.sl.library.exceptions.RegistryObjectModificationException;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IConfigAnimation<T extends StaticAnimation> permits
        ConfigActionAnimationGroup, ConfigAttackAnimationGroup, ConfigStaticAnimationGroup {

    AnimationGroupType configGroupType();
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
                   """, this.getAccessor(), this.animation(), this.configGroupType()
            );
            ConfigAnimationsErrorPool.addError(error);
            return false;
        }
        return true;
    }

    default boolean isInvalidAccessor() {
        return !isValidAccessor();
    }

    void applyConfig(IAnimationProperty<T> property);

    default void isSuccessful() {
        ExtendedDatapacks.LOGGER.info(
                "[<I> - Config Animation] Successfully processed animation config: {}",
                this.getAccessor()
        );
    }
}
