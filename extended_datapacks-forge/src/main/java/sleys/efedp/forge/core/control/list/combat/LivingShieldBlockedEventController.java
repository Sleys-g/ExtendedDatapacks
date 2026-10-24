package sleys.efedp.forge.core.control.list.combat;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

public record LivingShieldBlockedEventController() implements IControlableEventParams<Event> {

    public static final MapCodec<LivingShieldBlockedEventController> CODEC = MapCodec.unit(LivingShieldBlockedEventController::new);

    @Override
    public void applyEventParams(Event event, SkillContainer container) {}
}