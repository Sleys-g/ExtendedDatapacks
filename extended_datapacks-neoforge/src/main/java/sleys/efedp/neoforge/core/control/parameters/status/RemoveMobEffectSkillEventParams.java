package sleys.efedp.neoforge.core.control.parameters.status;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record RemoveMobEffectSkillEventParams() implements ISkillEventParams<MobEffectEvent.Remove> {

    public static final MapCodec<RemoveMobEffectSkillEventParams> CODEC = MapCodec.unit(RemoveMobEffectSkillEventParams::new);

    @Override
    public void applyEventParams(MobEffectEvent.Remove event, SkillContainer container) {}
}
