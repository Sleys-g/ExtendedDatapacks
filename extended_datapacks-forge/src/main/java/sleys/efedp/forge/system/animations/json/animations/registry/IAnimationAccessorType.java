package sleys.efedp.forge.system.animations.json.animations.registry;

import sleys.efedp.forge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.sl.library.annotations.Internal;

@Internal
public interface IAnimationAccessorType {
    String id();
    AnimationAccessorDefinitionCodec<?> definitionCodec();
}
