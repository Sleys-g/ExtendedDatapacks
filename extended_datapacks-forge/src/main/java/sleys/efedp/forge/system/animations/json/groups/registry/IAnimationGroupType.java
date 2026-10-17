package sleys.efedp.forge.system.animations.json.groups.registry;

import sleys.efedp.forge.system.animations.json.definitions.AnimationConfigDefinitionCodec;
import sleys.efedp.forge.system.animations.json.definitions.AnimationVirtualizationDefinitionCodec;
import sleys.sl.library.annotations.Internal;

@Internal
public interface IAnimationGroupType {
    String id();
    AnimationVirtualizationDefinitionCodec<?> virtualDefinitionCodec();
    AnimationConfigDefinitionCodec<?> configDefinitionCodec();
}
