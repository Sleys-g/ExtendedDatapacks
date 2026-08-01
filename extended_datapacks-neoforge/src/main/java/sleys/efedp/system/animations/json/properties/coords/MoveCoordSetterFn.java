package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;

public enum MoveCoordSetterFn implements ICoordFunction<MoveCoordFunctions.MoveCoordSetter> {
    RAW_COORD("raw_coord", MoveCoordFunctions.RAW_COORD),
    RAW_COORD_WITH_X_ROT("raw_coord_with_x_rot", MoveCoordFunctions.RAW_COORD_WITH_X_ROT),
    TRACE_ORIGIN_AS_DESTINATION("trace_origin_as_destination", MoveCoordFunctions.TRACE_ORIGIN_AS_DESTINATION),
    TRACE_TARGET_DISTANCE("trace_target_distance", MoveCoordFunctions.TRACE_TARGET_DISTANCE),
    TRACE_TARGET_LOCATION_ROTATION("trace_target_location_rotation", MoveCoordFunctions.TRACE_TARGET_LOCATION_ROTATION),
    VEX_TRACE("vex_trace", MoveCoordFunctions.VEX_TRACE);

    public final String id;
    public final MoveCoordFunctions.MoveCoordSetter setter;

    MoveCoordSetterFn(String id, MoveCoordFunctions.MoveCoordSetter setter) {
        this.id = id;
        this.setter = setter;
    }

    public static final Codec<MoveCoordSetterFn> CODEC = EnumCodecs.byId(values(), c -> c.id);

    @Override
    public MoveCoordFunctions.MoveCoordSetter value() {
        return this.setter;
    }
}
