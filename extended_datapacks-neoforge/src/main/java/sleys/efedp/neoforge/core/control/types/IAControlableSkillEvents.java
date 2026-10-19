package sleys.efedp.neoforge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.GenericSkillEventControl;
import sleys.efedp.neoforge.core.control.ISkillEventController;
import sleys.efedp.neoforge.core.control.parameters.ia.ClientTargetEntitySkillEventParams;
import sleys.efedp.neoforge.core.control.parameters.ia.ServerTargetEntitySkillEventParams;
import sleys.efedp.neoforge.core.control.parameters.ia.TargetEntitySkillEventParams;
import sleys.efedp.neoforge.core.control.registry.IControlableSkillEventType;

public enum IAControlableSkillEvents implements IControlableSkillEventType {

    TARGET_ENTITY_EVENT(GenericSkillEventControl.codec(TargetEntitySkillEventParams.CODEC)),
    SERVER_TARGET_ENTITY_EVENT(GenericSkillEventControl.codec(ServerTargetEntitySkillEventParams.CODEC)),
    CLIENT_TARGET_ENTITY_EVENT(GenericSkillEventControl.codec(ClientTargetEntitySkillEventParams.CODEC));

    private final MapCodec<? extends ISkillEventController<? extends Event>> codec;

    IAControlableSkillEvents(MapCodec<? extends ISkillEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends ISkillEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}