package sleys.efedp.neoforge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.GenericSkillEventControl;
import sleys.efedp.neoforge.core.control.ISkillEventController;
import sleys.efedp.neoforge.core.control.parameters.loot.LivingDropsSkillEventParams;
import sleys.efedp.neoforge.core.control.parameters.loot.LivingExperienceDropSkillEventParams;
import sleys.efedp.neoforge.core.control.registry.IControlableSkillEventType;

public enum LootControlableSkillEvents implements IControlableSkillEventType {

    LIVING_DROPS_EVENT(GenericSkillEventControl.codec(LivingDropsSkillEventParams.CODEC)),
    LIVING_EXPERIENCE_DROP_EVENT(GenericSkillEventControl.codec(LivingExperienceDropSkillEventParams.CODEC));

    private final MapCodec<? extends ISkillEventController<? extends Event>> codec;

    LootControlableSkillEvents(MapCodec<? extends ISkillEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends ISkillEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}
