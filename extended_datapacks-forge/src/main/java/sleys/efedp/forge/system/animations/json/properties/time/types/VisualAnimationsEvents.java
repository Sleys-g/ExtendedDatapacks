package sleys.efedp.forge.system.animations.json.properties.time.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.system.animations.json.properties.time.events.*;
import sleys.efedp.forge.system.animations.json.properties.time.registry.IAnimationEventType;

public enum VisualAnimationsEvents implements IAnimationEventType {
    FRACTURE_GROUND(FractureGroundEvent.CODEC),
    CAMERA_TRANSITION(CameraTransitionEvent.CODEC),
    WHITE_AFTERIMAGE(WhiteAfterImageEvent.CODEC),
    ENTITY_AFTERIMAGE(EntityAfterImageEvent.CODEC),

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    VisualAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
