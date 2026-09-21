package sleys.efedp.neoforge.world.entitypatchs;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import sleys.efedp.neoforge.registry.ExtendedDatapacksEntities;
import sleys.efedp.neoforge.world.entities.OwnableAnimatedPlayer;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.registry.entries.EpicFightAttributes;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

public class OwnableAnimatedPlayerPatch extends HumanoidMobPatch<OwnableAnimatedPlayer> {

    private static final String TASK_ID = "Ownable Animated Player Patch";

    public OwnableAnimatedPlayerPatch(OwnableAnimatedPlayer original) {
        super(original, Factions.NEUTRAL);
        this.setWeaponMotions();
    }

    @Override
    public void onJoinWorld(OwnableAnimatedPlayer entity, Level level, boolean worldGenSpawn) {
        super.onJoinWorld(entity, level, worldGenSpawn);

        if (level.isClientSide) return;

        var owner = entity.getOwner();
        if (owner == null) return;
        this.original.copySlots();

        var ownerAttributes = owner.getAttributes();
        var cloneAttributes = entity.getAttributes();

        ownerAttributes.getSyncableAttributes().forEach(ownerInstance -> {
            Holder<Attribute> attribute = ownerInstance.getAttribute();

            var cloneInstance = cloneAttributes.getInstance(attribute);
            if (cloneInstance == null) return;

            cloneInstance.setBaseValue(ownerInstance.getBaseValue());
        });
    }

    @Override @ErrorHandled
    public void preTick() {
        ExecutionTasks.run(ExecutionPolicy.RESIST, ErrorPolicy.DEPURATE, TASK_ID, super::preTick); /// Move Entity -> ResultProtocol<Entity>
        if (this.isLogicalClient()) return;

        if (!this.original.getNeedDiscord()) {
            var animation = this.original.getAnimation();
            if (animation == null) return;

            this.playAnimationSynchronized(animation, 0F);
            this.original.setNeedDiscard(true);
        } else {
            var entityAnimator = this.getServerAnimator();
            var entityAnimatorPlayer = entityAnimator.animationPlayer;
            if (entityAnimatorPlayer == null) return;
            var entityAnimation = entityAnimatorPlayer.getRealAnimation();

            if (this.original.getAnimation() == entityAnimation) return;
            this.original.discard();
        }
    }

    public void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.SNEAK, Animations.BIPED_SNEAK);
        animator.addLivingAnimation(LivingMotions.SWIM, Animations.BIPED_SWIM);
        animator.addLivingAnimation(LivingMotions.FLOAT, Animations.BIPED_FLOAT);
        animator.addLivingAnimation(LivingMotions.KNEEL, Animations.BIPED_KNEEL);
        animator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
        animator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
        animator.addLivingAnimation(LivingMotions.SIT, Animations.BIPED_SIT);
        animator.addLivingAnimation(LivingMotions.FLY, Animations.BIPED_FLYING);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
        animator.addLivingAnimation(LivingMotions.JUMP, Animations.BIPED_JUMP);
        animator.addLivingAnimation(LivingMotions.CLIMB, Animations.BIPED_CLIMBING);
        animator.addLivingAnimation(LivingMotions.SLEEP, Animations.BIPED_SLEEPING);
        animator.addLivingAnimation(LivingMotions.CREATIVE_FLY, Animations.BIPED_CREATIVE_FLYING);
        animator.addLivingAnimation(LivingMotions.CREATIVE_IDLE, Animations.BIPED_CREATIVE_IDLE);
        animator.addLivingAnimation(LivingMotions.DIGGING, Animations.BIPED_DIG);
        animator.addLivingAnimation(LivingMotions.AIM, Animations.BIPED_BOW_AIM);
        animator.addLivingAnimation(LivingMotions.SHOT, Animations.BIPED_BOW_SHOT);
        animator.addLivingAnimation(LivingMotions.DRINK, Animations.BIPED_DRINK);
        animator.addLivingAnimation(LivingMotions.EAT, Animations.BIPED_EAT);
        animator.addLivingAnimation(LivingMotions.SPECTATE, Animations.BIPED_SPYGLASS_USE);
    }

    @Override
    public void updateMotion(boolean b) {
        this.currentLivingMotion = LivingMotions.IDLE;
    }

    public static void initAttributes(EntityAttributeModificationEvent event) {
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.OFFHAND_ATTACK_SPEED);
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.OFFHAND_MAX_STRIKES);
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.OFFHAND_ARMOR_NEGATION);
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.OFFHAND_IMPACT);
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.WEIGHT);
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.ARMOR_NEGATION);
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.IMPACT);
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.MAX_STRIKES);
        event.add(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), EpicFightAttributes.STUN_ARMOR);
    }
}
