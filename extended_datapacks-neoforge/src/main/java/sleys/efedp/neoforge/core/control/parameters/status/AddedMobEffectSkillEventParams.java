package sleys.efedp.neoforge.core.control.parameters.status;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record AddedMobEffectSkillEventParams() implements ISkillEventParams<MobEffectEvent.Added> {

    public static final MapCodec<AddedMobEffectSkillEventParams> CODEC = MapCodec.unit(AddedMobEffectSkillEventParams::new);

    @Override
    public void applyEventParams(MobEffectEvent.Added event, SkillContainer container) {}
}