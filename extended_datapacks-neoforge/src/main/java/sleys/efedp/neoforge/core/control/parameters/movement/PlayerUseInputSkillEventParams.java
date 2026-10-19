package sleys.efedp.neoforge.core.control.parameters.movement;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import sleys.sl.epicfight.events.EFKeyUseInputEvent;
import yesman.epicfight.skill.SkillContainer;

public record PlayerUseInputSkillEventParams() implements ISkillEventParams<EFKeyUseInputEvent.UseInputEvent> {

    public static final MapCodec<PlayerUseInputSkillEventParams> CODEC = MapCodec.unit(PlayerUseInputSkillEventParams::new);

    @Override
    public void applyEventParams(EFKeyUseInputEvent.UseInputEvent event, SkillContainer container) {}
}