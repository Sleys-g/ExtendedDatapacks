package sleys.efedp.system.animations.json.properties.coords;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationProperty;

public enum AnimationCoordType {
    COORD_SET_BEGIN("coord_set_begin", AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordSetterFn.CODEC),
    COORD_SET_TICK("coord_set_tick", AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordSetterFn.CODEC),
    COORD_GET("coord_get", AnimationProperty.ActionAnimationProperty.COORD_GET, MoveCoordGetterFn.CODEC),
    DEST_LOCATION_PROVIDER("dest_location_provider", AnimationProperty.ActionAnimationProperty.DEST_LOCATION_PROVIDER, DestLocationProviderFn.CODEC),
    ENTITY_YROT_PROVIDER("entity_yrot_provider", AnimationProperty.ActionAnimationProperty.ENTITY_YROT_PROVIDER, YRotProviderFn.CODEC),
    DEST_COORD_YROT_PROVIDER("dest_coord_yrot_provider", AnimationProperty.ActionAnimationProperty.DEST_COORD_YROT_PROVIDER, YRotProviderFn.CODEC);

    public final String id;
    public final AnimationProperty.ActionAnimationProperty<?> actionAnimationProperty;
    public final Codec<? extends ICoordFunction<?>> functionCodec;

    AnimationCoordType(String id, AnimationProperty.ActionAnimationProperty<?> actionAnimationProperty,
                       Codec<? extends ICoordFunction<?>> functionCodec) {
        this.id = id;
        this.actionAnimationProperty = actionAnimationProperty;
        this.functionCodec = functionCodec;
    }

    public static final Codec<AnimationCoordType> CODEC = EnumCodecs.byId(values(), c -> c.id);
}