package sleys.efedp.neoforge.system.animations.json.animations.registry;

import sleys.efedp.neoforge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.sl.library.annotations.Internal;

@Internal
public interface IAnimationAccessorType {
    String id();
    AnimationAccessorDefinitionCodec<?> definitionCodec();
}
