package sleys.efedp.system.skills;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.system.skills.json.PassiveSkillBuilderModifier;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

public class ExtendedDatapacksRegistryPassives {

    @SubscribeEvent
    public static void addAnyParameterToSkill(SkillBuildEvent.ModRegistryWorker.SkillCreateEvent<?> event) {
        var skillRegistry = event.getRegistryName();
        if (skillRegistry == null) return;

        var skillBuilder = event.getSkillBuilder();
        if (skillBuilder == null) return;
        if (skillBuilder instanceof GuardSkill.Builder) return;

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
                    ExtendedDatapacks.LOGGER.fatal("[Passive Skill Mutator] Failed to assign Weapon Category {}", weaponCategory, e);
                    ExtendedDatapacks.LOGGER.warn("[Passive Skill Mutator] Error captured, ignore this message");
                }
            } else {
                ExtendedDatapacks.LOGGER.warn("[Passive Skill Mutator] Set<WeaponCategory> was not found");
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
