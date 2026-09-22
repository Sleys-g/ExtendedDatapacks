package sleys.efedp.neoforge.system.animations.json.properties.armature;

import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

public enum EpicFightArmatureTypes implements IArmatureType {
    BIPED("epicfight:entity/biped", Armatures.BIPED),
    CREEPER("epicfight:entity/creeper", Armatures.CREEPER),
    ENDERMAN("epicfight:entity/enderman", Armatures.ENDERMAN),
    SKELETON("epicfight:entity/skeleton", Armatures.SKELETON),
    SPIDER("epicfight:entity/spider", Armatures.SPIDER),
    IRON_GOLEM("epicfight:entity/iron_golem", Armatures.IRON_GOLEM),
    RAVAGER("epicfight:entity/ravager", Armatures.RAVAGER),
    VEX("epicfight:entity/vex", Armatures.VEX),
    PIGLIN("epicfight:entity/piglin", Armatures.PIGLIN),
    HOGLIN("epicfight:entity/hoglin", Armatures.HOGLIN),
    DRAGON("epicfight:entity/dragon", Armatures.DRAGON),
    WITHER("epicfight:entity/wither", Armatures.WITHER);

    public final String id;
    public final AssetAccessor<? extends Armature> accessor;

    EpicFightArmatureTypes(String id, AssetAccessor<? extends Armature> accessor) {
        this.id = id;
        this.accessor = accessor;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public AssetAccessor<? extends Armature> armature() {
        return accessor;
    }
}
