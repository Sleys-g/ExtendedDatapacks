package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingShieldBlockSkillEventParams() implements ISkillEventParams<LivingShieldBlockEvent> {

    public static final MapCodec<LivingShieldBlockSkillEventParams> CODEC = MapCodec.unit(LivingShieldBlockSkillEventParams::new);

    @Override
    public void applyEventParams(LivingShieldBlockEvent event, SkillContainer container) {}
}