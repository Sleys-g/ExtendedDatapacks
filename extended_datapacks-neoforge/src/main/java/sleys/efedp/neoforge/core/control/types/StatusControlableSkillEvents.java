package sleys.efedp.neoforge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.ISkillEventController;
import sleys.efedp.neoforge.core.control.GenericSkillEventControl;
import sleys.efedp.neoforge.core.control.parameters.status.*;
import sleys.efedp.neoforge.core.control.registry.IControlableSkillEventType;

public enum StatusControlableSkillEvents implements IControlableSkillEventType {
    LIVING_HEAL_EVENT(GenericSkillEventControl.codec(LivingHealSkillEventParams.CODEC)),

    ADDED_MOB_EFFECT_EVENT(GenericSkillEventControl.codec(AddedMobEffectSkillEventParams.CODEC)),
    REMOVE_MOB_EFFECT_EVENT(GenericSkillEventControl.codec(RemoveMobEffectSkillEventParams.CODEC)),
    EXPIRED_MOB_EFFECT_EVENT(GenericSkillEventControl.codec(ExpiredMobEffectSkillEventParams.CODEC)),
    APPLICABLE_MOB_EFFECT_EVENT(GenericSkillEventControl.codec(ApplicableMobEffectSkillEventParams.CODEC));

    private final MapCodec<? extends ISkillEventController<? extends Event>> codec;

    StatusControlableSkillEvents(MapCodec<? extends ISkillEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends ISkillEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}