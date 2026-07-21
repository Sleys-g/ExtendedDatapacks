package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;

public enum AnimationMoveCoordFunctions {
    MODEL_COORD("model_coord",
            null,
            MoveCoordFunctions.MODEL_COORD,
            null,
            null
    ),
    WORLD_COORD("world_coord",
            null,
            MoveCoordFunctions.WORLD_COORD,
            null,
            null
    ),
    ATTACHED("attached",
            null,
            MoveCoordFunctions.ATTACHED,
            null,
            null
    ),
    NO_DEST("no_dest",
            null,
            null,
            MoveCoordFunctions.NO_DEST,
            null
    ),
    ATTACK_TARGET_LOCATION("attack_target_location",
            null,
            null,
            MoveCoordFunctions.ATTACK_TARGET_LOCATION,
            null
    ),
    SYNCHED_DEST_VARIABLE("synched_dest_variable",
            null,
            null,
            MoveCoordFunctions.SYNCHED_DEST_VARIABLE,
            null
    ),
    SYNCHED_TARGET_ENTITY_LOCATION_VARIABLE("synched_target_entity_location_variable",
            null,
            null,
            MoveCoordFunctions.SYNCHED_DEST_VARIABLE,
            null
    ),
    LOOK_DEST("look_dest",
            null,
            null,
            null,
            MoveCoordFunctions.LOOK_DEST
    ),
    MOB_ATTACK_TARGET_LOOK("mob_attack_target_look",
            null,
            null,
            null,
            MoveCoordFunctions.MOB_ATTACK_TARGET_LOOK
    ),
    RAW_COORD("raw_coord",
            MoveCoordFunctions.RAW_COORD,
            null,
            null,
            null
    ),
    RAW_COORD_WITH_X_ROT("raw_coord_with_x_rot",
            MoveCoordFunctions.RAW_COORD_WITH_X_ROT,
            null,
            null,
            null
    ),
    TRACE_ORIGIN_AS_DESTINATION("trace_origin_as_destination",
            MoveCoordFunctions.TRACE_ORIGIN_AS_DESTINATION,
            null,
            null,
            null
    ),
    TRACE_TARGET_DISTANCE("trace_target_distance",
            MoveCoordFunctions.TRACE_TARGET_DISTANCE,
            null,
            null,
            null
    ),
    TRACE_TARGET_LOCATION_ROTATION("trace_target_location_rotation",
            MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION,
            null,
            null,
            null
    ),
    VEX_TRACE("vex_trace",
            MoveCoordFunctions.VEX_TRACE,
            null,
            null,
            null
    );

    public final String id;
    public final MoveCoordFunctions.MoveCoordSetter setter;
    public final MoveCoordFunctions.MoveCoordGetter getter;
    public final AnimationProperty.DestLocationProvider destLocationProvider;
    public final AnimationProperty.YRotProvider yRotProvider;

    AnimationMoveCoordFunctions(String id,
                                MoveCoordFunctions.MoveCoordSetter setter,
                                MoveCoordFunctions.MoveCoordGetter getter,
                                AnimationProperty.DestLocationProvider destLocationProvider,
                                AnimationProperty.YRotProvider yRotProvider) {
        this.id = id;
        this.setter = setter;
        this.getter = getter;
        this.destLocationProvider = destLocationProvider;
        this.yRotProvider = yRotProvider;
    }

    public static final Codec<AnimationMoveCoordFunctions> CODEC = EnumCodecs.byId(values(), c -> c.id);
}
