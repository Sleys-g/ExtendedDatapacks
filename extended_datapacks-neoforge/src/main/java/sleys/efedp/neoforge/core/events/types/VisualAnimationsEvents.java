package sleys.efedp.neoforge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.events.list.visual.CameraTransitionEvent;
import sleys.efedp.neoforge.core.events.list.visual.EntityAfterImageEvent;
import sleys.efedp.neoforge.core.events.list.visual.FractureGroundEvent;
import sleys.efedp.neoforge.core.events.list.visual.WhiteAfterImageEvent;
import sleys.efedp.neoforge.core.events.IAnimationEventParams;
import sleys.efedp.neoforge.core.events.registry.IAnimationEventType;

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
