package sleys.efedp.forge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.controlable.EventControl;
import sleys.efedp.forge.core.control.controlable.IEventController;
import sleys.efedp.forge.core.control.list.status.*;
import sleys.efedp.forge.core.control.registry.IControlableEventType;

public enum StatusControlableEvents implements IControlableEventType {
    LIVING_HEAL_EVENT(EventControl.codec(LivingHealEventController.CODEC)),

    ADDED_MOB_EFFECT_EVENT(EventControl.codec(AddedMobEffectEventController.CODEC)),
    REMOVE_MOB_EFFECT_EVENT(EventControl.codec(RemoveMobEffectEventController.CODEC)),
    EXPIRED_MOB_EFFECT_EVENT(EventControl.codec(ExpiredMobEffectEventController.CODEC)),
    APPLICABLE_MOB_EFFECT_EVENT(EventControl.codec(ApplicableMobEffectEventController.CODEC));

    private final MapCodec<? extends IEventController<? extends Event>> codec;

    StatusControlableEvents(MapCodec<? extends IEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}