package sleys.efedp.neoforge.api.external;

import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.controlable.IEventController;

public interface IExternalEventController<E extends Event> extends IEventController<E> {}
