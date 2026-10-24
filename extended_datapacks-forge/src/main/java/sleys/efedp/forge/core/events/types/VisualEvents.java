package sleys.efedp.forge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.core.events.IEventParams;
import sleys.efedp.forge.core.events.list.visual.CameraTransitionEvent;
import sleys.efedp.forge.core.events.list.visual.EntityAfterImageEvent;
import sleys.efedp.forge.core.events.list.visual.FractureGroundEvent;
import sleys.efedp.forge.core.events.list.visual.WhiteAfterImageEvent;
import sleys.efedp.forge.core.events.registry.IEventType;

public enum VisualEvents implements IEventType {
    FRACTURE_GROUND(FractureGroundEvent.CODEC),
    CAMERA_TRANSITION(CameraTransitionEvent.CODEC),
    WHITE_AFTERIMAGE(WhiteAfterImageEvent.CODEC),
    ENTITY_AFTERIMAGE(EntityAfterImageEvent.CODEC),

    ;private final MapCodec<? extends IEventParams> codec;

    VisualEvents(MapCodec<? extends IEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventParams> paramsCodec() {
        return codec;
    }
}
