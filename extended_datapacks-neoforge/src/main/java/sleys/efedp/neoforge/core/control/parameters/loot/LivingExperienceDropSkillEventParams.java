package sleys.efedp.neoforge.core.control.parameters.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingExperienceDropSkillEventParams() implements ISkillEventParams<LivingExperienceDropEvent> {

    public static final MapCodec<LivingExperienceDropSkillEventParams> CODEC = MapCodec.unit(LivingExperienceDropSkillEventParams::new);

    @Override
    public void applyEventParams(LivingExperienceDropEvent event, SkillContainer container) {}
}