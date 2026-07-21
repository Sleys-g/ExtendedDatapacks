package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.EnumCodecs;

public enum MoveAnimationType {
    /// Cords
    COORD_SET_BEGIN("coord_set_begin"),
    COORD_SET_TICK("coord_set_tick"),
    COORD_GET("coord_get"),

    /// Location
    DEST_LOCATION_PROVIDER("dest_location_provider"),

    /// Y Provider
    ENTITY_YROT_PROVIDER("entity_yrot_provider"),
    DEST_COORD_YROT_PROVIDER("dest_coord_yrot_provider")

    ;public final String id;

    MoveAnimationType(String id) {
        this.id = id;
    }

    public static final Codec<MoveAnimationType> CODEC = EnumCodecs.byId(values(), c -> c.id);
}
