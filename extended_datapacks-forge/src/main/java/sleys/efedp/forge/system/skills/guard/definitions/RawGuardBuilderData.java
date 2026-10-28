package sleys.efedp.forge.system.skills.guard.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public record RawGuardBuilderData(String skillId, String categoryId,
                                  GuardMotionData guardMotion,
                                  GuardMotionData guardBreakMotion,
                                  @Nullable GuardMotionData guardAdvancedMotion) {
    public static final Codec<RawGuardBuilderData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("skill_guard_id")
                            .forGetter(RawGuardBuilderData::skillId),

                    Codec.STRING.fieldOf("category")
                            .forGetter(RawGuardBuilderData::categoryId),

                    GuardMotionData.CODEC.fieldOf("guard_motion")
                            .forGetter(RawGuardBuilderData::guardMotion),

                    GuardMotionData.CODEC.fieldOf("guard_break_motion")
                            .forGetter(RawGuardBuilderData::guardBreakMotion),

                    GuardMotionData.CODEC.optionalFieldOf("guard_advanced_motion")
                            .forGetter(data -> Optional.ofNullable(data.guardAdvancedMotion()))
            ).apply(instance, (skillId,
                               categoryId,
                               guard,
                               breakMotion,
                               advanced) ->
                    new RawGuardBuilderData(
                            skillId,
                            categoryId,
                            guard,
                            breakMotion,
                            advanced.orElse(null)
                    )
            ));

    public boolean hasAdvancedMotion() {
        return guardAdvancedMotion != null;
    }

    public String getGuardMotionForStyle(String style) {
        return guardMotion.getAnimationForStyle(style);
    }

    public String getGuardBreakMotionForStyle(String style) {
        return guardBreakMotion.getAnimationForStyle(style);
    }

    @Nullable
    public String getAdvancedMotionForStyle(String style) {
        return guardAdvancedMotion == null
                ? null
                : guardAdvancedMotion.getAnimationForStyle(style);
    }

    @Nullable
    public List<String> getAdvancedMotionsForStyle(String style) {
        return guardAdvancedMotion == null
                ? null
                : guardAdvancedMotion.getAnimationsForStyle(style);
    }

    @Nullable
    public AnimationManager.AnimationAccessor<? extends StaticAnimation>
    getParseMotions(String animation) {
        ResourceLocation id = ResourceLocation.tryParse(animation);
        return id == null ? null : AnimationManager.byKey(id);
    }

    @Nullable
    public ResourceLocation getParseSkill() {
        return ResourceLocation.tryParse(skillId);
    }

    public WeaponCategory getParseWeaponCategory() {
        return WeaponCategory.ENUM_MANAGER.getOrThrow(categoryId);
    }

    public Style getParseStyle(String style) {
        return Style.ENUM_MANAGER.getOrThrow(style);
    }

    public boolean isSameSkill(ResourceLocation skillRegistry, ResourceLocation skillParsed) {
        return skillRegistry.equals(skillParsed);
    }
}
