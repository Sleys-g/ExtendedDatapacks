package sleys.efedp.neoforge.core.control.parameters.status;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record ApplicableMobEffectSkillEventParams() implements ISkillEventParams<MobEffectEvent.Applicable> {

    public static final MapCodec<ApplicableMobEffectSkillEventParams> CODEC = MapCodec.unit(ApplicableMobEffectSkillEventParams::new);

    @Override
    public void applyEventParams(MobEffectEvent.Applicable event, SkillContainer container) {}
}
