package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum AnimationCoordType {
    COORD_SET_BEGIN("coord_set_begin"),
    COORD_SET_TICK("coord_set_tick"),
    COORD_GET("coord_get"),
    DEST_LOCATION_PROVIDER("dest_location_provider"),
    ENTITY_YROT_PROVIDER("entity_yrot_provider"),
    DEST_COORD_YROT_PROVIDER("dest_coord_yrot_provider");

    public final String id;

    AnimationCoordType(String id) {
        this.id = id;
    }

    public static final Codec<AnimationCoordType> CODEC = EnumCodecs.byId(values(), c -> c.id);
}
