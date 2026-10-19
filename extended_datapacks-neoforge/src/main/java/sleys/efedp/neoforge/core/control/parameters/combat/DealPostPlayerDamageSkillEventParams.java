package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record DealPostPlayerDamageSkillEventParams() implements ISkillEventParams<LivingDamageEvent.Post> {

    public static final MapCodec<DealPostPlayerDamageSkillEventParams> CODEC = MapCodec.unit(DealPostPlayerDamageSkillEventParams::new);

    @Override
    public void applyEventParams(LivingDamageEvent.Post event, SkillContainer container) {}
}