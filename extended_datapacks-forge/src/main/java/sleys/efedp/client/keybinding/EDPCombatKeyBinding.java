package sleys.efedp.client.keybinding;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EDPCombatKeyBinding {
    public static KeyMapping chargedAttackKeyBinding;

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        chargedAttackKeyBinding = new KeyMapping(
                "epicfight_edp.key.charged_attack",
                InputConstants.Type.KEYSYM,
                86,
                "epicfight_edp.key.config_name"
        );
        event.register(chargedAttackKeyBinding);
    }
}
