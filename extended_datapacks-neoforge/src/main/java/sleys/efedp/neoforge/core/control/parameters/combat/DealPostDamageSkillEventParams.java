package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record DealPostDamageSkillEventParams() implements ISkillEventParams<LivingDamageEvent.Post> {

    public static final MapCodec<DealPostDamageSkillEventParams> CODEC = MapCodec.unit(DealPostDamageSkillEventParams::new);

    @Override
    public void applyEventParams(LivingDamageEvent.Post event, SkillContainer container) {}
}