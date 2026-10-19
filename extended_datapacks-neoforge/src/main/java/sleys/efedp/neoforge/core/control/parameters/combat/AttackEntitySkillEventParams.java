package sleys.efedp.neoforge.core.control.parameters.combat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import sleys.efedp.neoforge.core.control.ISkillEventParams;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import yesman.epicfight.skill.SkillContainer;

public record AttackEntitySkillEventParams() implements ISkillEventParams<AttackEntityEvent> {

    public static final MapCodec<AttackEntitySkillEventParams> CODEC = MapCodec.unit(AttackEntitySkillEventParams::new);

    @Override
    public void applyEventParams(AttackEntityEvent event, SkillContainer container) {}
}