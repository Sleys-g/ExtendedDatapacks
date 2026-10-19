package sleys.efedp.neoforge.core.control.parameters.movement;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import yesman.epicfight.skill.SkillContainer;

public record MovementInputSkillEventParams() implements ISkillEventParams<EFMovementInputEvent.InputEvent> {

    public static final MapCodec<MovementInputSkillEventParams> CODEC = MapCodec.unit(MovementInputSkillEventParams::new);

    @Override
    public void applyEventParams(EFMovementInputEvent.InputEvent event, SkillContainer container) {}
}