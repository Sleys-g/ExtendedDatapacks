package sleys.efedp.neoforge.core.control.parameters.movement;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import sleys.sl.epicfight.events.EFKeyUseInputEvent;
import yesman.epicfight.skill.SkillContainer;

public record PlayerServerUseInputSkillEventParams() implements ISkillEventParams<EFKeyUseInputEvent.ServerUseInputEvent> {

    public static final MapCodec<PlayerServerUseInputSkillEventParams> CODEC = MapCodec.unit(PlayerServerUseInputSkillEventParams::new);

    @Override
    public void applyEventParams(EFKeyUseInputEvent.ServerUseInputEvent event, SkillContainer container) {}
}