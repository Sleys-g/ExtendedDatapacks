package sleys.efedp.forge.system.category.definitions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record WeaponCategoryRegistryDefinitions(Optional<String> modId, Either<String, List<String>> category) {

    public record CategoryData(String modId, String categoryId) {}

    private static final Codec<WeaponCategoryRegistryDefinitions> ELEMENT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("modid")
                            .forGetter(WeaponCategoryRegistryDefinitions::modId),
                    Codec.either(Codec.STRING, Codec.STRING.listOf())
                            .fieldOf("category")
                            .forGetter(WeaponCategoryRegistryDefinitions::category)
            ).apply(instance, WeaponCategoryRegistryDefinitions::new));

    public static final Codec<WeaponCategoryRegistryDefinitions> CODEC =
            ELEMENT_CODEC.listOf().fieldOf("weapon_category_builder")
                    .codec()
                    .xmap(list -> list.isEmpty() ? new WeaponCategoryRegistryDefinitions(
                            Optional.empty(), Either.left("")) : list.get(0), List::of
                    );


    public List<CategoryData> toCategoryData(String resolvedModId) {
        String effectiveModId = modId.filter(id -> !id.isBlank()).orElse(resolvedModId);
        return category.map(
                single -> List.of(new CategoryData(effectiveModId, single)),
                multiple -> multiple.stream()
                        .map(id -> new CategoryData(effectiveModId, id))
                        .toList()
        );
    }
}