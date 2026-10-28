package sleys.efedp.forge.system.skills.passive;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.api.registry.RegistrySkillTrackers;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.weaponinnate.ConditionalWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public final class ModifyPassivesApplier {

    private ModifyPassivesApplier() {}

    @SubscribeEvent
    public static void addAnyParameterToSkill(SkillBuildEvent.ModRegistryWorker.SkillCreateEvent<?> event) {
        var skillRegistry = event.getRegistryName();
        if (skillRegistry == null) return;

        var skillBuilder = event.getSkillBuilder();
        if (skillBuilder == null) return;

        if (isExcludedSkillBuilder(skillBuilder)) return;

        Set<WeaponCategory> weaponCategorySet = findWeaponCategorySet(skillBuilder);
        if (weaponCategorySet == null) return;


        for (var dataSet : RegistrySkillTrackers.PASSIVE_SKILL_MODIFIER.getDefinitionData().values()) {
            for (var dataDefinition : dataSet) {
                for (var data : dataDefinition.skillBuilder()) {
                    if (!data.isSameSkill(skillRegistry)) continue;

                    var weaponCategory = data.getParseWeaponCategory();
                    if (weaponCategory == null) continue;

                    ExecutionTasks.runAndGetResult(ExecutionPolicy.RESIST, () -> weaponCategorySet.add(weaponCategory))
                            .peek(valid -> ExtendedDatapacks.LOGGER.info(
                                    "[Passive Skill Adder] The Passive Skill {} was successfully modified to add the weapon category {} to it.",
                                    data.skillId(), weaponCategory
                            ))
                            .peekError(error -> ExtendedDatapacks.LOGGER.warn(
                                    "[Passive Skill Adder] An error occurred while trying to assign WeaponCategory {}...",
                                    weaponCategory, error
                            ));
                }
            }
        }
    }

    private static boolean isExcludedSkillBuilder(SkillBuilder<?> skillBuilder) {
        if (skillBuilder == null) return true;
        else if (skillBuilder instanceof GuardSkill.Builder ignored) return true;
        else if (skillBuilder instanceof ConditionalWeaponInnateSkill.Builder ignored) return true;
        else return skillBuilder instanceof SimpleWeaponInnateSkill.Builder ignored;
    }

    @Nullable
    private static Set<WeaponCategory> findWeaponCategorySet(Object builder) {
        Class<?> clazz = builder.getClass();
        while (clazz != null && clazz != Object.class) {
            for (var field : clazz.getDeclaredFields()) {
                if (!Set.class.isAssignableFrom(field.getType())) continue;

                var genericType = field.getGenericType();
                if (!(genericType instanceof ParameterizedType paramType)) continue;

                Type[] args = paramType.getActualTypeArguments();
                if (!(args.length == 1 && args[0] == WeaponCategory.class)) continue;

                return ExecutionTasks.get(ExecutionPolicy.RESIST, () -> ModifyPassivesApplier.weaponCategories(field, builder));
            }

            clazz = clazz.getSuperclass();
        }

        return null;
    }

    private static Set<WeaponCategory> weaponCategories(Field field, Object builder) throws IllegalAccessException {
        field.setAccessible(true);
        if (!(field.get(builder) instanceof Set<?> set)) return null;

        @SuppressWarnings("unchecked") Set<WeaponCategory> weaponSet = (Set<WeaponCategory>) set;
        return weaponSet;
    }
}
