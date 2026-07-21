package sleys.efedp.system.skills;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.skills.json.PassiveSkillBuilderModifier;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.weaponinnate.ConditionalWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public class ExtendedDatapacksRegistryPassives {

    /**
     * @author Sleys
     * @apiNote Permite añadir cualquier categoria a una Passive Skills, se intuye que el usuario suministrara la categoria
     * objetivo desde JSON, al igual que la Passive Skill. Se excluye Weapon Passive e Weapon Innate porque operan desde otro campo
     * y ya se pueden añadir.
     */
    public static void addAnyParameterToSkill(ResourceLocation skillRegistry, SkillBuilder<?> skillBuilder) {
        if (skillRegistry == null) return;

        switch (skillBuilder) {
            case null -> {
                return;
            }
            case GuardSkill.Builder ignored -> {
                return;
            }
            case ConditionalWeaponInnateSkill.Builder ignored -> {
                return;
            }
            case SimpleWeaponInnateSkill.Builder ignored -> {
                return;
            }
            case WeaponInnateSkill.Builder<?> ignored -> {
                return;
            }
            default -> {}
        }

        var dataList = PassiveSkillBuilderModifier.get(skillRegistry.toString());
        if (dataList == null || dataList.isEmpty()) return;
        for (var data : dataList) {
            var weaponCategory = data.getParseWeaponCategory();
            var skillID = data.getParseSkill();
            if (!data.isSameSkill(skillRegistry, skillID)) continue;
            Set<WeaponCategory> set = ExtendedDatapacksRegistryPassives.findWeaponCategorySet(skillBuilder);
            if (set != null) {
                try {
                    set.add(weaponCategory);
                } catch (Exception e) {
                    ExtendedDatapacks.LOGGER.fatal("[Passive Skill Mutator] A fatal error ocurred while trying to assign WeaponCategory {}", weaponCategory, e);
                    ExtendedDatapacks.LOGGER.warn("[Passive Skill Mutator] Caught error, ignore this message");
                }
            } else {
                    ExtendedDatapacks.LOGGER.warn("[Passive Skill Mutator] Set<WeaponCategory> not found");
            }
        }
    }

    @Nullable
    public static Set<WeaponCategory> findWeaponCategorySet(Object builder) {
        Class<?> clazz = builder.getClass();
        while (clazz != null && clazz != Object.class) {
            for (var field : clazz.getDeclaredFields()) {

                if (!Set.class.isAssignableFrom(field.getType())) {
                    ExtendedDatapacks.LOGGER.warn("[Passive Skill Mutator] There is no Set<WeaponCategory> field, the bet failed");
                    continue;
                }

                var genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType paramType) {
                    Type[] args = paramType.getActualTypeArguments();
                    if (args.length == 1 && args[0] == WeaponCategory.class) {

                        try {
                            field.setAccessible(true);
                            var value = field.get(builder);

                            if (value instanceof Set<?> set) {
                                @SuppressWarnings("unchecked") Set<WeaponCategory> weaponSet = (Set<WeaponCategory>) set;
                                return weaponSet;
                            }
                        } catch (IllegalAccessException ignored) {}
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
