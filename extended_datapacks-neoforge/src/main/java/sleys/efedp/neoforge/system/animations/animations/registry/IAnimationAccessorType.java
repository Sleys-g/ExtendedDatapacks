package sleys.efedp.neoforge.system.animations.animations.registry;

import sleys.efedp.neoforge.system.animations.definitions.AnimationAccessorDefinitionCodec;
import sleys.sl.library.annotations.Internal;

@Internal
public interface IAnimationAccessorType {
    String id();
    AnimationAccessorDefinitionCodec<?> definitionCodec();
}
