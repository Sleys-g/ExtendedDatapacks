package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingUseTotemEventParams() implements ISkillEventParams<LivingUseTotemEvent> {

    public static final MapCodec<LivingUseTotemEventParams> CODEC = MapCodec.unit(LivingUseTotemEventParams::new);

    @Override
    public void applyEventParams(LivingUseTotemEvent event, SkillContainer container) {}
}