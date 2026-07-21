package sleys.efedp.capability.skills;

import net.minecraft.nbt.CompoundTag;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.ConditionsType;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MultiConditionalWeaponInnateSkill extends WeaponInnateSkill {
    protected final Map<ConditionsType, ConditionData> conditionMap;

    public MultiConditionalWeaponInnateSkill(MultiConditionalWeaponInnateSkill.Builder builder) {
        super(builder);
        this.conditionMap = builder.conditionMap;
    }

    public static Builder createMultiConditionalBuilder() {
        return new Builder(MultiConditionalWeaponInnateSkill::new)
                .setCategory(SkillCategories.WEAPON_INNATE)
                .setResource(Resource.WEAPON_CHARGE);
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        for (var entry : this.conditionMap.values()) {
            ExecutionTasks.operateAndGetResult(
                    ExecutionPolicy.RESIST,
                    entry, this::registryAnimationsData
            ).ifFailure(e ->
                    ExtendedDatapacks.LOGGER.error(
                            "[Multi Conditional - InnateSkill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                            this.registryName.getPath(), this.registryName.getNamespace()
                    )
            );
        }
        return this;
    }

    private MultiConditionalWeaponInnateSkill.ConditionData registryAnimationsData(MultiConditionalWeaponInnateSkill.ConditionData entry) {
        var animation = entry.animationAccessor().get();
        if (!(animation instanceof AttackAnimation attack)) {
            ExtendedDatapacks.LOGGER.warn("[Multi Conditional - InnateSkill] The animation: {}, It is NOT an attack animation or one that inherits from it; it will proceed, however, the attempt to apply properties is suppressed....", animation);
            return null;
        }

        for (int i = 0; i < attack.phases.length; i++) {
            if (i < entry.properties().size()) {
                attack.phases[i].addProperties(entry.properties().get(i).entrySet());
            }
        }
        return entry;
    }

    @Override
    public void executeOnServer(SkillContainer container, CompoundTag args) {
        this.playSkillAnimation(container.getServerExecutor());
        super.executeOnServer(container, args);
    }

    protected void playSkillAnimation(ServerPlayerPatch executor) {
        var player = executor.getOriginal();
        ConditionsType activeCondition = ConditionsType.NORMAL;

        if (!player.onGround() && this.conditionMap.containsKey(ConditionsType.IN_AIR)) {
            activeCondition = ConditionsType.IN_AIR;
        } else if (player.isSprinting() && this.conditionMap.containsKey(ConditionsType.SPRINTING)) {
            activeCondition = ConditionsType.SPRINTING;
        } else if (player.getPersistentData().getBoolean("minecraft:use_item/event/lazy")
                && this.conditionMap.containsKey(ConditionsType.USE_ITEM)) {
            activeCondition = ConditionsType.USE_ITEM;
        } else if (player.isShiftKeyDown() && this.conditionMap.containsKey(ConditionsType.KNEELING)) {
            activeCondition = ConditionsType.KNEELING;
        }

        var dataToPlay = this.conditionMap.get(activeCondition);
        if (dataToPlay != null) {
            executor.playAnimationSynchronized(dataToPlay.animationAccessor().get().getRealAnimation(), 0.0F);
        }
    }

    public record ConditionData(
            AnimationManager.AnimationAccessor<? extends DynamicAnimation> animationAccessor,
            List<Map<AnimationProperty.AttackPhaseProperty<?>, Object>> properties
    ) {}

    public static class Builder extends WeaponInnateSkill.Builder<MultiConditionalWeaponInnateSkill.Builder> {
        protected final Map<ConditionsType, ConditionData> conditionMap = new EnumMap<>(ConditionsType.class);

        public Builder(Function<MultiConditionalWeaponInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public Builder addConditionData(ConditionsType type, ConditionData data) {
            this.conditionMap.put(type, data);
            return this;
        }
    }
}