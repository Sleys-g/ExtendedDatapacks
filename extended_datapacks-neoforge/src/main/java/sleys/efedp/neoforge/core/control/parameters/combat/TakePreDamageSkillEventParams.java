package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record TakePreDamageSkillEventParams() implements ISkillEventParams<LivingDamageEvent.Pre> {

    public static final MapCodec<TakePreDamageSkillEventParams> CODEC = MapCodec.unit(TakePreDamageSkillEventParams::new);

    @Override
    public void applyEventParams(LivingDamageEvent.Pre event, SkillContainer container) {}
}