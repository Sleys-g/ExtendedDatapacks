package sleys.efedp.neoforge.core.control.parameters.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingDropsSkillEventParams() implements ISkillEventParams<LivingDropsEvent> {

    public static final MapCodec<LivingDropsSkillEventParams> CODEC = MapCodec.unit(LivingDropsSkillEventParams::new);

    @Override
    public void applyEventParams(LivingDropsEvent event, SkillContainer container) {}
}