package sleys.efedp.mixins.epicfight;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sleys.efedp.system.weapons.json.WeaponItemsProperties;

@Mixin(Item.class)
public class ItemMixinClient {

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "getUseAnimation", cancellable = true, at = @At("HEAD"))
    private void onUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> cir) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (!(player instanceof Player nplayer)) return;

        var interactionOptional = WeaponItemsProperties.getSafeEntryAsInteraction(InteractionHand.MAIN_HAND, nplayer);
        if (interactionOptional.isEmpty()) return;

        var interaction = interactionOptional.get();
        if (!interaction.isDisableUseAnimation()) return;

        cir.setReturnValue(UseAnim.NONE);
    }
}
