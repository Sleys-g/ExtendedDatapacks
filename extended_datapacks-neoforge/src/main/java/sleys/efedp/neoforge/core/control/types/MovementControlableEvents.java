package sleys.efedp.neoforge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.controlable.EventControl;
import sleys.efedp.neoforge.core.control.controlable.IEventController;
import sleys.efedp.neoforge.core.control.list.movement.*;
import sleys.efedp.neoforge.core.control.registry.IControlableEventType;

public enum MovementControlableEvents implements IControlableEventType {
    LIVING_BREATH_EVENT(EventControl.codec(LivingBreathEventController.CODEC)),
    LIVING_FALL_EVENT(EventControl.codec(LivingFallEventController.CODEC)),

    MOVEMENT_INPUT_EVENT(EventControl.codec(MovementInputEventController.CODEC)),

    PLAYER_CLIENT_USE_INPUT_EVENT(EventControl.codec(PlayerClientUseInputEventController.CODEC)),
    PLAYER_SERVER_USE_INPUT_EVENT(EventControl.codec(PlayerServerUseInputEventController.CODEC)),
    PLAYER_USE_INPUT_EVENT(EventControl.codec(PlayerUseInputEventController.CODEC));

    private final MapCodec<? extends IEventController<? extends Event>> codec;

    MovementControlableEvents(MapCodec<? extends IEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}
