package sleys.efedp.neoforge.core.control.types;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.core.control.GenericSkillEventControl;
import sleys.efedp.neoforge.core.control.ISkillEventController;
import sleys.efedp.neoforge.core.control.parameters.movement.*;
import sleys.efedp.neoforge.core.control.registry.IControlableSkillEventType;

public enum MovementControlableSkillEvents implements IControlableSkillEventType {
    LIVING_BREATH_EVENT(GenericSkillEventControl.codec(LivingBreathSkillEventParams.CODEC)),
    LIVING_FALL_EVENT(GenericSkillEventControl.codec(LivingBreathSkillEventParams.CODEC)),

    MOVEMENT_INPUT_EVENT(GenericSkillEventControl.codec(MovementInputSkillEventParams.CODEC)),

    PLAYER_CLIENT_USE_INPUT_EVENT(GenericSkillEventControl.codec(PlayerClientUseInputSkillEventParams.CODEC)),
    PLAYER_SERVER_USE_INPUT_EVENT(GenericSkillEventControl.codec(PlayerServerUseInputSkillEventParams.CODEC)),
    PLAYER_USE_INPUT_EVENT(GenericSkillEventControl.codec(PlayerUseInputSkillEventParams.CODEC));

    private final MapCodec<? extends ISkillEventController<? extends Event>> codec;

    MovementControlableSkillEvents(MapCodec<? extends ISkillEventController<? extends Event>> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends ISkillEventController<? extends Event>> controllerCodec() {
        return codec;
    }
}
