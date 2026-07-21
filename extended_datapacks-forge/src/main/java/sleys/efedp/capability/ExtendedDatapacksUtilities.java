package sleys.efedp.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.skill.Skill;

public class ExtendedDatapacksUtilities {

    public static Item getSafeItem(String namespace, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        return ForgeRegistries.ITEMS.getValue(id);
    }

    public static Skill getSafeSkill(String namespace, String path) {
        return SkillManager.getSkill(namespace + ":" + path);
    }
}
