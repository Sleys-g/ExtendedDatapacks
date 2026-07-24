package sleys.efedp.capability.skills;

import net.minecraft.nbt.CompoundTag;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.innates.json.ConditionsType;
import sleys.sl.library.annotations.ErrorHandled;
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

public class StacksMultiConditionalWeaponInnateSkill extends WeaponInnateSkill {
    protected final Map<ConditionsType, ConditionData> conditionMap;
    protected final Map<ConditionsType, Integer> stacksMap;

    public StacksMultiConditionalWeaponInnateSkill(StacksMultiConditionalWeaponInnateSkill.Builder builder) {
        super(builder);
        this.conditionMap = builder.conditionMap;
        this.stacksMap = builder.stacksMap;
    }

    public static Builder createMultiConditionalBuilder() {
        return new Builder(StacksMultiConditionalWeaponInnateSkill::new)
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
                            "[Stacks Multi Conditional - InnateSkill] Fatal error caught during property assignment attempt... For Skill: {}, under NameSpaces: {}",
                            this.registryName.getPath(), this.registryName.getNamespace()
                    )
            );
        }
        return this;
    }

    @ErrorHandled
    private StacksMultiConditionalWeaponInnateSkill.ConditionData registryAnimationsData(StacksMultiConditionalWeaponInnateSkill.ConditionData entry) {
        var animation = entry.animationAccessor().get();
        if (!(animation instanceof AttackAnimation attack)) {
            ExtendedDatapacks.LOGGER.warn("[Stacks Multi Conditional - InnateSkill] The animation: {}, It is NOT an attack animation or one that inherits from it; it will proceed, however, the attempt to apply properties is suppressed....", animation);
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
        var stacksToPlay = this.stacksMap.get(activeCondition);
        var skillContainer = executor.getSkill(this);
        var skillStacks = skillContainer.getStack();

        var isCreative = player.isCreative();
        if (dataToPlay != null && (skillStacks >= stacksToPlay || isCreative)) {
            executor.playAnimationSynchronized(dataToPlay.animationAccessor().get().getRealAnimation(), 0.0F);
            if (!isCreative) this.setStackSynchronize(skillContainer, (skillStacks - stacksToPlay));
        }
    }

    public record ConditionData(
            AnimationManager.AnimationAccessor<? extends DynamicAnimation> animationAccessor,
            List<Map<AnimationProperty.AttackPhaseProperty<?>, Object>> properties
    ) {}

    public static class Builder extends WeaponInnateSkill.Builder<StacksMultiConditionalWeaponInnateSkill.Builder> {
        protected final Map<ConditionsType, ConditionData> conditionMap = new EnumMap<>(ConditionsType.class);
        protected final Map<ConditionsType, Integer> stacksMap = new EnumMap<>(ConditionsType.class);

        public Builder(Function<StacksMultiConditionalWeaponInnateSkill.Builder, ? extends Skill> constructor) {
            super(constructor);
        }

        public Builder addConditionData(ConditionsType type, ConditionData data) {
            this.conditionMap.put(type, data);
            return this;
        }

        public Builder addStacksData(ConditionsType type, Integer stack) {
            this.stacksMap.put(type, stack);
            return this;
        }
    }
}