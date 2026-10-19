package sleys.efedp.neoforge.core.control.parameters.movement;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import sleys.sl.epicfight.events.EFKeyUseInputEvent;
import yesman.epicfight.skill.SkillContainer;

public record PlayerClientUseInputSkillEventParams() implements ISkillEventParams<EFKeyUseInputEvent.ClientUseInputEvent> {

    public static final MapCodec<PlayerClientUseInputSkillEventParams> CODEC = MapCodec.unit(PlayerClientUseInputSkillEventParams::new);

    @Override
    public void applyEventParams(EFKeyUseInputEvent.ClientUseInputEvent event, SkillContainer container) {}
}