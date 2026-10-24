package sleys.efedp.forge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.controlable.EventControl;
import sleys.efedp.forge.core.control.controlable.IEventController;
import sleys.efedp.forge.core.control.list.ticks.ClientTickEventController;
import sleys.efedp.forge.core.control.list.ticks.ServerTickEventController;
import sleys.efedp.forge.core.control.list.ticks.TickEventController;
import sleys.efedp.forge.core.control.registry.IControlableEventType;

public enum TickControlableEvents implements IControlableEventType {

    CLIENT_TICK_EVENT(EventControl.codec(ClientTickEventController.CODEC)),
    SERVER_TICK_EVENT(EventControl.codec(ServerTickEventController.CODEC)),
    TICK_EVENT(EventControl.codec(TickEventController.CODEC)),

    ;private final MapCodec<? extends IEventController<? extends Event>> codec;

    TickControlableEvents(MapCodec<? extends IEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}
