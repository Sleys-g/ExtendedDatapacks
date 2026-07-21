package sleys.efedp.capability.skill;

import net.minecraft.client.KeyMapping;
import sleys.sl.epicfight.skills.interfaces.movement.IOnMovementInputEFSkillEvent;
import sleys.sl.library.runtime.policy.error.ErrorPolicy;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.SynchedAnimationVariableKeys;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.modules.ChargeableSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;

public abstract class HoldableInnateSkill extends WeaponInnateSkill implements ChargeableSkill, IOnMovementInputEFSkillEvent {
    private final AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimation;
    private final AnimationManager.AnimationAccessor<? extends AttackAnimation> attackAnimation;

    public HoldableInnateSkill(SkillBuilder<? extends WeaponInnateSkill> builder,
                               AnimationManager.AnimationAccessor<? extends StaticAnimation> charging,
                               AnimationManager.AnimationAccessor<? extends AttackAnimation> attack) {
        super(builder);
        this.chargingAnimation = charging;
        this.attackAnimation = attack;
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        ErrorPolicy.DEPURATE_ERROR.executeTaskWithMsg(
                this::registryAnimationsData,
                "[Holdable - InnateSkill] Fatal error caught during property assignment attempt... " +
                        "For Skill: "  + this.registryName.getPath() +
                        ", under NameSpaces: " + this.registryName.getNamespace()
        );
        return this;
    }

    private void registryAnimationsData() {
        AttackAnimation anim = this.attackAnimation.get();
        for(AttackAnimation.Phase phase : anim.phases) {
            phase.addProperties(this.properties.get(0).entrySet());
        }
    }

    @Override
    public void startHolding(SkillContainer container) {
        AssetAccessor<? extends StaticAnimation> currentPlaying =
                container.getExecutor()
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
    public void onStopHolding(SkillContainer container, SPSkillExecutionFeedback feedback) {
        container.getExecutor().getAnimator().getVariables().put(
                SynchedAnimationVariableKeys.CHARGING_TICKS.get(),
                this.attackAnimation,
                container.getExecutor().getAccumulatedChargeAmount()
        );

        container.getExecutor().playAnimationSynchronized(this.attackAnimation, 0.0F);
        this.cancelOnServer(container, null);
    }

    @Override
    public void holdTick(SkillContainer container) {
        ChargeableSkill.super.holdTick(container);
    }

    @Override
    public KeyMapping getKeyMapping() {
        return EpicFightKeyMappings.WEAPON_INNATE_SKILL;
    }
}
