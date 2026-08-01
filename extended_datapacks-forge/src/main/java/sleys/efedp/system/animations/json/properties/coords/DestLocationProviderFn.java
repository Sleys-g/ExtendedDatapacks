package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;

public enum DestLocationProviderFn {
    NO_DEST("no_dest", MoveCoordFunctions.NO_DEST),
    ATTACK_TARGET_LOCATION("attack_target_location", MoveCoordFunctions.ATTACK_TARGET_LOCATION),
    SYNCHED_DEST_VARIABLE("synched_dest_variable", MoveCoordFunctions.SYNCHED_DEST_VARIABLE),
    SYNCHED_TARGET_ENTITY_LOCATION_VARIABLE("synched_target_entity_location_variable", MoveCoordFunctions.SYNCHED_DEST_VARIABLE);

    public final String id;
    public final AnimationProperty.DestLocationProvider destLocationProvider;

    DestLocationProviderFn(String id, AnimationProperty.DestLocationProvider destLocationProvider) {
        this.id = id;
        this.destLocationProvider = destLocationProvider;
    }

    public static final Codec<DestLocationProviderFn> CODEC = EnumCodecs.byId(values(), c -> c.id);
}
