package sleys.efedp.forge.world.entities;

import net.minecraftforge.event.entity.living.LivingDamageEvent;

public interface IControlableEntityDamage {
    void entityHurtEvent(LivingDamageEvent event);
}
