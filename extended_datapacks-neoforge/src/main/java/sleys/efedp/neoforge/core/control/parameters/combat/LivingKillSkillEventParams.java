package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingKillSkillEventParams() implements ISkillEventParams<LivingDeathEvent> {

    public static final MapCodec<LivingKillSkillEventParams> CODEC = MapCodec.unit(LivingKillSkillEventParams::new);

    @Override
    public void applyEventParams(LivingDeathEvent event, SkillContainer container) {}
}