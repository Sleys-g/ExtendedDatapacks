package sleys.efedp.system.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import sleys.efedp.system.weapons.json.WeaponItemsProperties;
import sleys.sl.epicfight.util.helper.animation.AnimationHelper;
import sleys.sl.library.execution.task.CoroutineTask;
import sleys.sl.library.util.helper.player.PlayerHelper;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class WeaponItemsPropertiesCoroutineRunner extends CoroutineTask {

    public WeaponItemsPropertiesCoroutineRunner() {}

    public enum UsingState {
        IDLE, USE
    }

    public UsingState usingState = UsingState.IDLE;

    @Override
    protected boolean run() {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (!PlayerHelper.isValidPlayer(player)) return true;

        var localPatch = EpicFightCapabilities.getLocalPlayerPatch(player);
        if (localPatch == null) return true;

        var interactionOptional = WeaponItemsProperties.getSafeEntryAsInteraction(InteractionHand.MAIN_HAND, player);
        if (interactionOptional.isEmpty()) return true;

        var interaction = interactionOptional.get();
        var useAnimation = interaction.parsedUseAnimation();
        if (useAnimation == null) return true;

        var animationKey = AnimationManager.byKey(useAnimation);
        if (animationKey == null) return true;

        var animation = AnimationHelper.getStaticAnimationAccessor(animationKey);
        if (animation.isEmpty()) return true;


        this.onUsingItem(mc, localPatch, animation);
        return true;
    }

    private void onUsingItem(Minecraft mc, LocalPlayerPatch localPatch,
                             AnimationManager.AnimationAccessor<? extends StaticAnimation> animation) {

        if (mc.options.keyUse.isDown() && !mc.options.keyAttack.isDown()) {
            if (usingState.equals(UsingState.IDLE)) {
                usingState = UsingState.USE;
                localPatch.playAnimationSynchronized(animation.get().getRealAnimation(), 0.1F);
            }
        } else if (usingState.equals(UsingState.USE)) {
            usingState = UsingState.IDLE;
            localPatch.stopPlaying(animation);
        }
    }
}
