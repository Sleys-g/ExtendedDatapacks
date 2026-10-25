package sleys.efedp.neoforge.system.skills.passive.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

public record RawSkillBuilderData(String skillId, String categoryId) {

    public static final Codec<RawSkillBuilderData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("skill_id")
                            .forGetter(RawSkillBuilderData::skillId),
                    Codec.STRING.fieldOf("category")
                            .forGetter(RawSkillBuilderData::categoryId)
            ).apply(instance, RawSkillBuilderData::new));

    public ResourceLocation getParseSkill() {
        return ResourceLocation.tryParse(skillId);
    }

    public WeaponCategory getParseWeaponCategory() {
        return WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
    }

    public boolean isSameSkill(ResourceLocation skillRegistry, ResourceLocation skillParsed) {
        return skillRegistry.equals(skillParsed);
    }

    public boolean isSameSkill(ResourceLocation skillRegistry) {
        return getParseSkill().equals(skillRegistry);
    }
}