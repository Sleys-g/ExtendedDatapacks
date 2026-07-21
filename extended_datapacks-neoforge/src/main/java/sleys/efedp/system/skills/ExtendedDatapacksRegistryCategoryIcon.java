package sleys.efedp.system.skills;

import net.minecraft.world.item.ItemStack;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.skills.json.SkillIconBuilderModifier;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.Map;

public class ExtendedDatapacksRegistryCategoryIcon {

    public static void AddAnyIconToCategory(Map<WeaponCategory, ItemStack> icon) {
        var itemKeyList = SkillIconBuilderModifier.getItemList();
        if (itemKeyList.isEmpty()) return;

        for (var itemKey : itemKeyList) {
            var data = SkillIconBuilderModifier.getIconData(itemKey);
            if (data == null) continue;

            var item = data.getParseItem(itemKey);
            if (item == null) continue;

            var categoryKey = data.categoryId();
            var category = data.getParseWeaponCategory(categoryKey);
            if (category == null) continue;

            try {
                icon.put(category, new ItemStack(item));
            } catch (Exception e) {
                ExtendedDatapacks.LOGGER.fatal("[Icon Category Mutator] A fatal error occurred while trying to register your icon: {}", e.getMessage());
                ExtendedDatapacks.LOGGER.warn("[Icon Category Mutator] Error captured, ignore this message");
            }
        }
    }
}
