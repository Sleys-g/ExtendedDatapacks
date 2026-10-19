package sleys.efedp.neoforge.core.control.parameters.ia;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.events.EFOnTargetEvent;
import yesman.epicfight.skill.SkillContainer;

public record TargetEntitySkillEventParams() implements ISkillEventParams<EFOnTargetEvent.EpicFightTargetEvent> {

    public static final MapCodec<TargetEntitySkillEventParams> CODEC = MapCodec.unit(TargetEntitySkillEventParams::new);

    @Override
    public void applyEventParams(EFOnTargetEvent.EpicFightTargetEvent event, SkillContainer container) {}
}