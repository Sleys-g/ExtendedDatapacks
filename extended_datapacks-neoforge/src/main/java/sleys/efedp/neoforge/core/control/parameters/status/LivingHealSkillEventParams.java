package sleys.efedp.neoforge.core.control.parameters.status;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingHealSkillEventParams() implements ISkillEventParams<LivingHealEvent> {

    public static final MapCodec<LivingHealSkillEventParams> CODEC = MapCodec.unit(LivingHealSkillEventParams::new);

    @Override
    public void applyEventParams(LivingHealEvent event, SkillContainer container) {}
}