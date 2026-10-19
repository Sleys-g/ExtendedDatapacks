package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingShieldBlockedSkillEventParams() implements ISkillEventParams<LivingShieldBlockEvent> {

    public static final MapCodec<LivingShieldBlockedSkillEventParams> CODEC = MapCodec.unit(LivingShieldBlockedSkillEventParams::new);

    @Override
    public void applyEventParams(LivingShieldBlockEvent event, SkillContainer container) {}
}