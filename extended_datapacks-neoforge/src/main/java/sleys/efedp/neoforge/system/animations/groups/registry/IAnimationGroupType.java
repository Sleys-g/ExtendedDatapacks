package sleys.efedp.neoforge.system.animations.groups.registry;

import sleys.efedp.neoforge.system.animations.definitions.AnimationConfigDefinitionCodec;
import sleys.efedp.neoforge.system.animations.definitions.AnimationVirtualizationDefinitionCodec;
import sleys.sl.library.annotations.Internal;

@Internal
public interface IAnimationGroupType {
    String id();
    AnimationVirtualizationDefinitionCodec<?> virtualDefinitionCodec();
    AnimationConfigDefinitionCodec<?> configDefinitionCodec();
}
