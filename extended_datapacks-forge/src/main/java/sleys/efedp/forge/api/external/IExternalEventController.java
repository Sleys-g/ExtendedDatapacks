package sleys.efedp.forge.api.external;

import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.controlable.IEventController;

public interface IExternalEventController<E extends Event> extends IEventController<E> {}
