package sleys.efedp.neoforge.system.animations.json.properties.phase.registry;

import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public interface IArmatureType {
    String id();
    AssetAccessor<? extends Armature> armature();
}
