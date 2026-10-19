package sleys.efedp.neoforge.core.control.parameters.movement;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingBreathSkillEventParams() implements ISkillEventParams<LivingBreatheEvent> {

    public static final MapCodec<LivingBreathSkillEventParams> CODEC = MapCodec.unit(LivingBreathSkillEventParams::new);

    @Override
    public void applyEventParams(LivingBreatheEvent event, SkillContainer container) {}
}