package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingIncomingPlayerDealDamageSkillEventParams() implements ISkillEventParams<LivingIncomingDamageEvent> {

    public static final MapCodec<LivingIncomingPlayerDealDamageSkillEventParams> CODEC = MapCodec.unit(LivingIncomingPlayerDealDamageSkillEventParams::new);

    @Override
    public void applyEventParams(LivingIncomingDamageEvent event, SkillContainer container) {}
}