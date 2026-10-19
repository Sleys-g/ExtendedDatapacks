package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingIncomingTakeDamageSkillEventParams() implements ISkillEventParams<LivingIncomingDamageEvent> {

    public static final MapCodec<LivingIncomingTakeDamageSkillEventParams> CODEC = MapCodec.unit(LivingIncomingTakeDamageSkillEventParams::new);

    @Override
    public void applyEventParams(LivingIncomingDamageEvent event, SkillContainer container) {}
}