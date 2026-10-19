package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingDeathSkillEventParams() implements ISkillEventParams<LivingDeathEvent> {

    public static final MapCodec<LivingDeathSkillEventParams> CODEC = MapCodec.unit(LivingDeathSkillEventParams::new);

    @Override
    public void applyEventParams(LivingDeathEvent event, SkillContainer container) {}
}