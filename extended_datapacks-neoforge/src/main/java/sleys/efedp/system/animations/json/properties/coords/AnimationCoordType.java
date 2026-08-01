package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.types.ActionAnimation;

public enum AnimationCoordType {
    COORD_SET_BEGIN("coord_set_begin", CoordBeginSetter.codec()),
    COORD_SET_TICK("coord_set_tick", CoordTickSetter.codec()),
    COORD_GET("coord_get", CoordGetter.codec()),
    DEST_LOCATION_PROVIDER("dest_location_provider", DestLocationProvider.codec()),
    ENTITY_YROT_PROVIDER("entity_yrot_provider", EntityYRotProvider.codec()),
    DEST_COORD_YROT_PROVIDER("dest_coord_yrot_provider", DestCoordYRotProvider.codec());

    public final String id;
    public final MapCodec<? extends IAnimationCoord<?>> codec;

    AnimationCoordType(String id, MapCodec<? extends IAnimationCoord<?>> codec) {
        this.id = id;
        this.codec = codec;
    }

    public static final Codec<AnimationCoordType> CODEC =
            EnumCodecs.byId(values(), c -> c.id);

    @SuppressWarnings("unchecked")
    public <T extends ActionAnimation> MapCodec<? extends IAnimationCoord<T>> codec() {
        return (MapCodec<? extends IAnimationCoord<T>>) codec;
    }
}