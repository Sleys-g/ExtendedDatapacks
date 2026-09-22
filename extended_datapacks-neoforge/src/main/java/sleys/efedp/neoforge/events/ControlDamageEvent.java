package sleys.efedp.neoforge.events;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.neoforge.world.entities.IControlableEntityDamage;

public class ControlDamageEvent {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCloneDamage(LivingDamageEvent.Pre event) {
        var entity = event.getSource().getDirectEntity();
        if (!(entity instanceof IControlableEntityDamage cdEntity)) return;
        cdEntity.entityHurtEvent(event);
    }
}
