package sleys.efedp.forge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.controlable.EventControl;
import sleys.efedp.forge.core.control.controlable.IEventController;
import sleys.efedp.forge.core.control.list.loot.LivingDropsEventController;
import sleys.efedp.forge.core.control.list.loot.LivingExperienceDropEventController;
import sleys.efedp.forge.core.control.registry.IControlableEventType;

public enum LootControlableEvents implements IControlableEventType {

    LIVING_DROPS_EVENT(EventControl.codec(LivingDropsEventController.CODEC)),
    LIVING_EXPERIENCE_DROP_EVENT(EventControl.codec(LivingExperienceDropEventController.CODEC));

    private final MapCodec<? extends IEventController<? extends Event>> codec;

    LootControlableEvents(MapCodec<? extends IEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}
