package sleys.efedp.neoforge.world.entities;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public interface IControlableEntityDamage {
    void entityHurtEvent(LivingDamageEvent.Pre event);
}
