package sleys.efedp.world.entitypatchs;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import sleys.efedp.registry.ExtendedDatapacksEntities;
import sleys.efedp.world.entities.OwnableWitherGhost;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class OwnableWitherGhostPatch extends MobPatch<OwnableWitherGhost> {

    @Override
    public void onJoinWorld(OwnableWitherGhost original, EntityJoinLevelEvent event) {
        super.onJoinWorld(original, event);

        if (!this.original.isNoAi()) {
            this.playAnimation(Animations.WITHER_CHARGE, 0.0F);

            if (this.isLogicalClient()) {
                this.playSound(SoundEvents.WITHER_AMBIENT, -0.1F, 0.1F);
            }
        }
    }

    @Override
    public void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.WITHER_IDLE);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.WITHER_IDLE);
    }

    public static void initAttributes(EntityAttributeModificationEvent event) {
        event.add(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), EpicFightAttributes.IMPACT.get(), 3.0D);
    }

    @Override
    public void updateMotion(boolean considerInaction) {
        this.currentLivingMotion = LivingMotions.IDLE;
    }

    @Override
    public AnimationManager.AnimationAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
        return null;
    }
}
