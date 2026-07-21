package sleys.efedp.system.animations.json.properties.phase;

import com.mojang.serialization.Codec;
import sleys.efedp.system.animations.json.properties.coords.MoveAnimationType;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;

import java.util.Arrays;

public enum ArmatureType {
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

    ArmatureType(String id, AssetAccessor<? extends Armature> accessor) {
        this.id = id;
        this.accessor = accessor;
    }

    public static final Codec<ArmatureType> CODEC = EnumCodecs.byId(values(), c -> c.id);
    public static ArmatureType fromAccessor(AssetAccessor<? extends Armature> accessor) {
        return Arrays.stream(values())
                .filter(a -> a.accessor == accessor)
                .findFirst()
                .orElseThrow(() -> new RegistryObjectException("[Armature Type] No ArmatureType found for accessor: " + accessor));
    }
}
