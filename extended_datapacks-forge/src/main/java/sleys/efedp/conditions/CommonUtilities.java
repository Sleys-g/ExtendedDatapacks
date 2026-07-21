package sleys.efedp.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataKeys;

@ApiStatus.Internal
public class CommonUtilities {

    public static boolean compareFloatValues(float actual, float expected, ComparisonType type, float tolerance) {
        return switch (type) {
            case NOT_EQUAL -> Math.abs(actual - expected) > tolerance;
            case GREATER_THAN -> actual > expected;
            case LESS_THAN -> actual < expected;
            case GREATER_THAN_OR_EQUAL -> actual >= expected;
            case LESS_THAN_OR_EQUAL -> actual <= expected;
            default -> Math.abs(actual - expected) <= tolerance;
        };
    }

    public static boolean compareIntegerValues(int actual, int expected, ComparisonType type) {
        return switch (type) {
            case NOT_EQUAL -> Math.abs(actual - expected) > 0;
            case GREATER_THAN -> actual > expected;
            case LESS_THAN -> actual < expected;
            case GREATER_THAN_OR_EQUAL -> actual >= expected;
            case LESS_THAN_OR_EQUAL -> actual <= expected;
            default -> Math.abs(actual - expected) == 0;
        };
    }

    @SuppressWarnings("all")
    public static SkillDataKey<?> getSkillDataKey(String name) {
        IForgeRegistry<SkillDataKey<?>> skillDataKey = SkillDataKeys.REGISTRY.get();
        ResourceLocation rl;
        if (name.indexOf(58) >= 0) {
            rl = ResourceLocation.parse(name);
        } else {
            rl = ResourceLocation.fromNamespaceAndPath("epicfight", name);
        }
        return skillDataKey.containsKey(rl) ? (SkillDataKey<?>) skillDataKey.getValue(rl) : null;
    }
}