package sleys.efedp.forge.system.skills.icon.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;

public record RawIconSkillBuilderData(String itemID, String categoryId) {

    public static final Codec<RawIconSkillBuilderData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("item_id")
                            .forGetter(RawIconSkillBuilderData::itemID),

                    Codec.STRING.fieldOf("category")
                            .forGetter(RawIconSkillBuilderData::categoryId)
            ).apply(instance, RawIconSkillBuilderData::new));

    public WeaponCategory getParseWeaponCategory() {
        return WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    public Item getParseItem() {
        var itemKey = ResourceLocation.tryParse(itemID);
        if (itemKey == null) return null;
        return BuiltInRegistries.ITEM.getOptional(itemKey).orElse(null);
    }
}
