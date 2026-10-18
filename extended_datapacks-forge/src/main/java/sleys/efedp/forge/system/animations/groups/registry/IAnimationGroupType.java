package sleys.efedp.forge.system.animations.groups.registry;

import sleys.efedp.forge.system.animations.definitions.AnimationConfigDefinitionCodec;
import sleys.efedp.forge.system.animations.definitions.AnimationVirtualizationDefinitionCodec;
import sleys.sl.library.annotations.Internal;

@Internal
public interface IAnimationGroupType {
    String id();
    AnimationVirtualizationDefinitionCodec<?> virtualDefinitionCodec();
    AnimationConfigDefinitionCodec<?> configDefinitionCodec();
}
