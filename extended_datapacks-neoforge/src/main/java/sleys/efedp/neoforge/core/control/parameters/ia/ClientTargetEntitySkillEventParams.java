package sleys.efedp.neoforge.core.control.parameters.ia;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.events.EFOnTargetEvent;
import yesman.epicfight.skill.SkillContainer;

public record ClientTargetEntitySkillEventParams() implements ISkillEventParams<EFOnTargetEvent.EpicFightClientTargetEvent> {

    public static final MapCodec<ClientTargetEntitySkillEventParams> CODEC = MapCodec.unit(ClientTargetEntitySkillEventParams::new);

    @Override
    public void applyEventParams(EFOnTargetEvent.EpicFightClientTargetEvent event, SkillContainer container) {}
}