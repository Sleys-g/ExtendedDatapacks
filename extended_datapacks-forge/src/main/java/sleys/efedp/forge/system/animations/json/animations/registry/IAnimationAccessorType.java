package sleys.efedp.forge.system.animations.json.animations.registry;

import sleys.efedp.forge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;

public interface IAnimationAccessorType {
    String id();
    AnimationAccessorDefinitionCodec<?> definitionCodec();
}
