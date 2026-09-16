package sleys.efedp.neoforge.system.animations.json.groups.registry;

import sleys.efedp.neoforge.system.animations.json.definitions.AnimationConfigDefinitionCodec;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationVirtualizationDefinitionCodec;

public interface IAnimationGroupType {
    String id();
    AnimationVirtualizationDefinitionCodec<?> virtualDefinitionCodec();
    AnimationConfigDefinitionCodec<?> configDefinitionCodec();
}
