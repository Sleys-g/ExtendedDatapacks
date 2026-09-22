package sleys.efedp.forge.events;

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.world.entities.IControlableEntityDamage;

public class ControlDamageEvent {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCloneDamage(LivingDamageEvent event) {
        var entity = event.getSource().getDirectEntity();
        if (!(entity instanceof IControlableEntityDamage cdEntity)) return;
        cdEntity.entityHurtEvent(event);
    }
}
