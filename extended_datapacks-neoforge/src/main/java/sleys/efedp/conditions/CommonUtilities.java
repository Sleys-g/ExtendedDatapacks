package sleys.efedp.conditions;

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
}