package sleys.efedp.neoforge.system.animations.properties.armature.registry;

import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

@Internal
public interface IArmatureType {
    String id();
    AssetAccessor<? extends Armature> armature();
}
