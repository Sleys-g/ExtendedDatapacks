package sleys.efedp.neoforge.system.skills.icon;

import net.minecraft.world.item.ItemStack;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.api.registry.RegistrySkillTrackers;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.Map;

public class RegistryCategoryIcon {

    public static void AddAnyIconToCategory(Map<WeaponCategory, ItemStack> icon) {
        for (var dataSet : RegistrySkillTrackers.CATEGORY_SKILL_ICON.getDefinitionData().values()) {
            for (var dataDefinition : dataSet) {
                for (var data : dataDefinition.iconSkillBuilder()) {
                    if (data == null) continue;

                    var item = data.getParseItem();
                    if (item == null) continue;

                    var category = data.getParseWeaponCategory();
                    if (category == null) continue;

                    var newItem = new ItemStack(item);
                    ExecutionTasks.runAndGetResult(ExecutionPolicy.RESIST, () -> icon.put(category, newItem))
                            .peek(valid -> ExtendedDatapacks.LOGGER.info(
                                    "[Icon Category Adder] Item {} has been successfully added as an icon to category {}",
                                    newItem, category
                            ))
                            .peekError(error -> ExtendedDatapacks.LOGGER.warn(
                                    "[Icon Category Adder] An error occurred while trying to assign a Icon to Weapon Category {} for item {}...",
                                    category, newItem, error
                            ));
                }
            }
        }
    }
}
