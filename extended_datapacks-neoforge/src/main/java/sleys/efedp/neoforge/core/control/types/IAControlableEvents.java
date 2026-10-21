package sleys.efedp.neoforge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.controlable.EventControl;
import sleys.efedp.neoforge.core.control.controlable.IEventController;
import sleys.efedp.neoforge.core.control.list.ia.ClientTargetEntityEventController;
import sleys.efedp.neoforge.core.control.list.ia.ServerTargetEntityEventController;
import sleys.efedp.neoforge.core.control.list.ia.TargetEntityEventController;
import sleys.efedp.neoforge.core.control.registry.IControlableEventType;

public enum IAControlableEvents implements IControlableEventType {

    TARGET_ENTITY_EVENT(EventControl.codec(TargetEntityEventController.CODEC)),
    SERVER_TARGET_ENTITY_EVENT(EventControl.codec(ServerTargetEntityEventController.CODEC)),
    CLIENT_TARGET_ENTITY_EVENT(EventControl.codec(ClientTargetEntityEventController.CODEC));

    private final MapCodec<? extends IEventController<? extends Event>> codec;

    IAControlableEvents(MapCodec<? extends IEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}