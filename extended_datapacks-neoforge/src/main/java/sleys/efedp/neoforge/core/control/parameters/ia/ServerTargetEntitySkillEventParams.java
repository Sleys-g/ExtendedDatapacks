package sleys.efedp.neoforge.core.control.parameters.ia;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.events.EFOnTargetEvent;
import yesman.epicfight.skill.SkillContainer;

public record ServerTargetEntitySkillEventParams() implements ISkillEventParams<EFOnTargetEvent.EpicFightServerTargetEvent> {

    public static final MapCodec<ServerTargetEntitySkillEventParams> CODEC = MapCodec.unit(ServerTargetEntitySkillEventParams::new);

    @Override
    public void applyEventParams(EFOnTargetEvent.EpicFightServerTargetEvent event, SkillContainer container) {}
}