package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum AnimationCoordType {
    COORD_SET_BEGIN("coord_set_begin", CoordBeginSetter.codec()),
    COORD_SET_TICK("coord_set_tick", CoordTickSetter.codec()),
    COORD_GET("coord_get", CoordGetter.codec()),
    DEST_LOCATION_PROVIDER("dest_location_provider", DestLocationProvider.codec()),
    ENTITY_YROT_PROVIDER("entity_yrot_provider", EntityYRotProvider.codec()),
    DEST_COORD_YROT_PROVIDER("dest_coord_yrot_provider", DestCoordYRotProvider.codec());

    public final String id;
    public final MapCodec<? extends IAnimationCoord<?>> animationCoordCodec;

    AnimationCoordType(String id, MapCodec<? extends IAnimationCoord<?>> animationCoordCodec) {
        this.id = id;
        this.animationCoordCodec = animationCoordCodec;
    }

    public static final Codec<AnimationCoordType> CODEC = EnumCodecs.byId(values(), c -> c.id);
}