package sleys.efedp.forge.core.control.list.combat;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingShieldBlockEventController() implements IControlableEventParams<Event> {

    public static final MapCodec<LivingShieldBlockEventController> CODEC = MapCodec.unit(LivingShieldBlockEventController::new);

    @Override
    public void applyEventParams(Event event, SkillContainer container) {}
}