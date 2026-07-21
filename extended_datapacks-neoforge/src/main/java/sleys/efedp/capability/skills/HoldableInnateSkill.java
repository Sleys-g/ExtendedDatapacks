package sleys.efedp.capability.skills;

import net.minecraft.client.KeyMapping;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.epicfight.skills.interfaces.movement.IOnMovementInputEFSkillEvent;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.side.ClientOnly;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.network.server.SPSkillFeedback;
import yesman.epicfight.registry.entries.EpicFightSynchedAnimationVariableKeys;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.modules.ChargeableSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;

public abstract class HoldableInnateSkill extends WeaponInnateSkill implements ChargeableSkill, IOnMovementInputEFSkillEvent  {
    private final AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation;
    private final AnimationManager.AnimationAccessor<? extends AttackAnimation> attackAnimation;

    public HoldableInnateSkill(WeaponInnateSkill.Builder<?> builder,
                               AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation,
                               AnimationManager.AnimationAccessor<? extends AttackAnimation> attackAnimation) {
        super(builder);
        this.chargingAnimation = chargingAnimation;
        this.attackAnimation = attackAnimation;
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        ExecutionTasks.runAndGetResult(
                ExecutionPolicy.RESIST,
                this::registryAnimationsData
        ).ifFailure(e -> ExtendedDatapacks.LOGGER.error(
                "[Holdable - InnateSkill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                this.registryName.getPath(), this.registryName.getNamespace()
        ));
        return this;
    }

    private void registryAnimationsData() {
        AttackAnimation anim = this.attackAnimation.get();
        for(AttackAnimation.Phase phase : anim.phases) {
            phase.addProperties(this.properties.getFirst().entrySet());
        }
    }

    @Override
    public void startHolding(SkillContainer container) {
        AssetAccessor<? extends StaticAnimation> currentPlaying = container
                .getExecutor()
                .getAnimator()
                .getPlayerFor(null)
                .getRealAnimation();

        if (currentPlaying.get().isMainFrameAnimation()) {
            container.getExecutor().stopPlaying(currentPlaying);
        }

        container.getExecutor().playAnimationSynchronized(this.chargingAnimation, 0.0F);
    }

    @Override
    public void resetHolding(SkillContainer container) {
        if (container.getExecutor().isLogicalClient()) {
            container.getExecutor().getAnimator().stopPlaying(this.chargingAnimation);
        } else {
            container.getExecutor().stopPlaying(this.chargingAnimation);
        }

    }

    @Override
    public void onStopHolding(SkillContainer container, SPSkillFeedback feedback) {
        container.getExecutor().getAnimator().getVariables().put(
                EpicFightSynchedAnimationVariableKeys.CHARGING_TICKS.get(),
                this.attackAnimation, container.getExecutor().getAccumulatedChargeTicks()
        );

        container.getExecutor().playAnimationSynchronized(this.attackAnimation, 0.0F);
        this.cancelOnServer(container, null);
    }

    @Override
    public void holdTick(SkillContainer container) {
        ChargeableSkill.super.holdTick(container);
    }

    @ClientOnly
    public KeyMapping getKeyMapping() {
        return EpicFightKeyMappings.WEAPON_INNATE_SKILL;
    }
}
