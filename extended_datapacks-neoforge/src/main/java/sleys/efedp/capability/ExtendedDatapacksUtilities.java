package sleys.efedp.capability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

public class ExtendedDatapacksUtilities {

    public static Item getSafeItem(String namespace, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        return BuiltInRegistries.ITEM.get(id);
    }

    public static Skill getSafeSkill(String namespace, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        return EpicFightRegistries.SKILL.get(id);
    }
}
