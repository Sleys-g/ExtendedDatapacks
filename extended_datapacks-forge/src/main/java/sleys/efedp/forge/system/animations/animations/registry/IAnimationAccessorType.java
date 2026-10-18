package sleys.efedp.forge.system.animations.animations.registry;

import sleys.efedp.forge.system.animations.definitions.AnimationAccessorDefinitionCodec;
import sleys.sl.library.annotations.Internal;

@Internal
public interface IAnimationAccessorType {
    String id();
    AnimationAccessorDefinitionCodec<?> definitionCodec();
}
