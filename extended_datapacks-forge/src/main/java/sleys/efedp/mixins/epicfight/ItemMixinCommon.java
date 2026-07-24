package sleys.efedp.mixins.epicfight;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sleys.efedp.system.weapons.json.WeaponItemsProperties;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(Item.class)
public class ItemMixinCommon {

    @SuppressWarnings("removal")
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand,
                       CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        var pachtPlayer = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
        if (pachtPlayer == null) return;

        var interactionOptional = WeaponItemsProperties.getSafeEntryAsInteraction(hand, player);
        if (interactionOptional.isEmpty()) return;

        var interaction = interactionOptional.get();
        if (!interaction.forceUseMethod()) return;

        var existPassive = pachtPlayer.getHoldingItemCapability(InteractionHand.MAIN_HAND).getPassiveSkill() != null;
        if (!existPassive) return;

        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.success(player.getItemInHand(hand)));
    }

    @Inject(method = "hurtEnemy", cancellable = true, at = @At("HEAD"))
    private void onHurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (!(player instanceof Player nplayer)) return;

        var combatOptional = WeaponItemsProperties.getSafeEntryAsCombat(player.swingingArm, nplayer);
        if (combatOptional.isEmpty()) return;

        var combat = combatOptional.get();
        if (!combat.disableHurtEnemy()) return;

        cir.setReturnValue(false);
    }
}
