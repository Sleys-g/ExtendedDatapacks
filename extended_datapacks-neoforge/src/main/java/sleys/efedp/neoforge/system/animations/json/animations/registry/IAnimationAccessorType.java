package sleys.efedp.neoforge.system.animations.json.animations.registry;

import sleys.efedp.neoforge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;

public interface IAnimationAccessorType {
    String id();
    AnimationAccessorDefinitionCodec<?> definitionCodec();
}
