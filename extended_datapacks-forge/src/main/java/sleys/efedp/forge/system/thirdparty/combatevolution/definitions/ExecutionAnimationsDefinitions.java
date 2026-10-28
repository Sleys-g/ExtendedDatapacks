package sleys.efedp.forge.system.thirdparty.combatevolution.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.shelmarow.combat_evolution.execution.ExecutionTypeManager;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionAttackAnimation;
import net.shelmarow.combat_evolution.gameassets.animation.ExecutionHitAnimation;
import sleys.efedp.forge.system.skills.guard.definitions.GuardSkillModifierDefinitions;
import sleys.efedp.forge.system.skills.guard.definitions.RawGuardBuilderData;
import sleys.sl.epicfight.capability.StyleInvalid;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import javax.annotation.Nullable;
import java.util.Locale;


public record ExecutionAnimationsDefinitions(String category,
                                             String item,
                                             String style,
                                             String executionAnimation,
                                             String executedAnimation,

                                             float xOffset,
                                             float yOffset,
                                             float zOffset,
                                             float rotationOffset,

                                             int totalTick) {

    public static final Codec<ExecutionAnimationsDefinitions> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("category").forGetter(ExecutionAnimationsDefinitions::category),
                    Codec.STRING.fieldOf("item").forGetter(ExecutionAnimationsDefinitions::item),
                    Codec.STRING.fieldOf("style").forGetter(ExecutionAnimationsDefinitions::style),
                    Codec.STRING.fieldOf("executionAnimation").forGetter(ExecutionAnimationsDefinitions::executionAnimation),
                    Codec.STRING.fieldOf("executedAnimation").forGetter(ExecutionAnimationsDefinitions::executedAnimation),

                    Codec.FLOAT.fieldOf("xOffset").forGetter(ExecutionAnimationsDefinitions::xOffset),
                    Codec.FLOAT.fieldOf("yOffset").forGetter(ExecutionAnimationsDefinitions::yOffset),
                    Codec.FLOAT.fieldOf("zOffset").forGetter(ExecutionAnimationsDefinitions::zOffset),
                    Codec.FLOAT.fieldOf("rotationOffset").forGetter(ExecutionAnimationsDefinitions::rotationOffset),

                    Codec.INT.fieldOf("totalTick").forGetter(ExecutionAnimationsDefinitions::totalTick)
            ).apply(instance, ExecutionAnimationsDefinitions::new));

    public boolean isNullItem() {
        return item.toLowerCase(Locale.ROOT).equals("null");
    }

    @Nullable
    public ResourceLocation getParseItemRegistry() {
        if (isNullItem()) return null;
        return ResourceLocation.tryParse(item);
    }

    @Nullable
    public Item getParseItem() {
        var parseRegistry = getParseItemRegistry();
        if (parseRegistry == null) return null;
        return ForgeRegistries.ITEMS.getValue(parseRegistry);
    }

    public Style getParseStyle() {
        if (style.equals("null")) return StyleInvalid.INVALID;
        return  Style.ENUM_MANAGER.getOrThrow(style);
    }

    public WeaponCategory getParsedWeaponCategory() {
        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                () -> WeaponCategory.ENUM_MANAGER.getOrThrow(category),
                null
        );
    }

    public ExecutionTypeManager.Type getParsedExecutionManager(
            AnimationManager.AnimationAccessor<StaticAnimation> executionAnimationKey,
            AnimationManager.AnimationAccessor<StaticAnimation> executionHitAnimationKey) {

        return new ExecutionTypeManager.Type(
                getExecutionAnimationAccessor(executionAnimationKey),
                getExecutionHitAnimationAccessor(executionHitAnimationKey),
                new Vec3(xOffset, yOffset, zOffset), rotationOffset, totalTick
        );
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private static AnimationManager.AnimationAccessor<? extends ExecutionAttackAnimation> getExecutionAnimationAccessor(
            AnimationManager.AnimationAccessor<StaticAnimation> animationKey) {

        return (AnimationManager.AnimationAccessor<? extends ExecutionAttackAnimation>) (AnimationManager.AnimationAccessor<?>) animationKey;
    }

    @SuppressWarnings({"unchecked", "RedundantCast"})
    private static AnimationManager.AnimationAccessor<? extends ExecutionHitAnimation> getExecutionHitAnimationAccessor(
            AnimationManager.AnimationAccessor<StaticAnimation> animationKey) {

        return (AnimationManager.AnimationAccessor<? extends ExecutionHitAnimation>) (AnimationManager.AnimationAccessor<?>) animationKey;
    }
}
