package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingKnockbackSkillEventParams() implements ISkillEventParams<LivingKnockBackEvent> {

    public static final MapCodec<LivingKnockbackSkillEventParams> CODEC = MapCodec.unit(LivingKnockbackSkillEventParams::new);

    @Override
    public void applyEventParams(LivingKnockBackEvent event, SkillContainer container) {}
}