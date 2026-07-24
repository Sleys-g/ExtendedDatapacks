package sleys.efedp.system.skills;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.skills.json.SkillIconBuilderModifier;
import yesman.epicfight.api.client.forgeevent.WeaponCategoryIconRegisterEvent;

public class ExtendedDatapacksRegistryCategoryIcon {

    @SubscribeEvent
    public static void onIconCreate(WeaponCategoryIconRegisterEvent icon) {
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
                icon.registerCategory(category, new ItemStack(item));
            } catch (Exception e) {
                ExtendedDatapacks.LOGGER.fatal("[Icon Category Mutator] A fatal error occurred while trying to register your icon: {}", e.getMessage());
                ExtendedDatapacks.LOGGER.warn("[Icon Category Mutator] Error captured, ignore this message");
            }
        }
    }
}
