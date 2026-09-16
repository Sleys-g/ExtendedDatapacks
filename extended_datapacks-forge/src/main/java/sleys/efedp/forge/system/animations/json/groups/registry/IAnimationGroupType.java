package sleys.efedp.forge.system.animations.json.groups.registry;

import sleys.efedp.forge.system.animations.json.definitions.AnimationConfigDefinitionCodec;
import sleys.efedp.forge.system.animations.json.definitions.AnimationVirtualizationDefinitionCodec;

public interface IAnimationGroupType {
    String id();
    AnimationVirtualizationDefinitionCodec<?> virtualDefinitionCodec();
    AnimationConfigDefinitionCodec<?> configDefinitionCodec();
}
