package sleys.efedp.system.animations.json.properties.functional.time.list;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.time.IAnimationEventType;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.*;

public enum VisualAnimationsEvents implements IAnimationEventType {
    FRACTURE_GROUND(FractureGroundEvent.CODEC),
    CAMERA_TRANSITION(CameraTransitionEvent.CODEC),
    WHITE_AFTERIMAGE(WhiteAfterImageEvent.CODEC),
    ENTITY_AFTERIMAGE(EntityAfterImageEvent.CODEC),

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    VisualAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
