package sleys.efedp.neoforge.core.control.parameters.movement;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingFallSkillEventParams() implements ISkillEventParams<LivingFallEvent> {

    public static final MapCodec<LivingFallSkillEventParams> CODEC = MapCodec.unit(LivingFallSkillEventParams::new);

    @Override
    public void applyEventParams(LivingFallEvent event, SkillContainer container) {}
}