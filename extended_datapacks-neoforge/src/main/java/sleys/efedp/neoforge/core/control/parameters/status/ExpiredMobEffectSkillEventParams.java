package sleys.efedp.neoforge.core.control.parameters.status;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record ExpiredMobEffectSkillEventParams() implements ISkillEventParams<MobEffectEvent.Expired> {

    public static final MapCodec<ExpiredMobEffectSkillEventParams> CODEC = MapCodec.unit(ExpiredMobEffectSkillEventParams::new);

    @Override
    public void applyEventParams(MobEffectEvent.Expired event, SkillContainer container) {}
}
