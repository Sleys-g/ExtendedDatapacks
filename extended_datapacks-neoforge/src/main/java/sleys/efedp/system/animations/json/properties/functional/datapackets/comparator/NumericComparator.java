package sleys.efedp.system.animations.json.properties.functional.datapackets.comparator;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum NumericComparator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_EQUAL,
    LESS_THAN,
    LESS_EQUAL,
    IN_RANGE,
    INCLUSIVE_UPPER_RANGE,
    INCLUSIVE_LOWER_RANGE,
    INCLUSIVE_RANGE

    ;public static final Codec<NumericComparator> CODEC = EnumCodecs.byId(values(), Enum::name);

    public <N extends Number> boolean comparate(Object actual, N upper) {
        return this.comparate(actual, upper, null);
    }

    public <N extends Number> boolean comparate(Object actual, N upper, @Nullable N lower) {
        var cmpUpper = switch (actual) {
            case Byte b -> Byte.compare(b, upper.byteValue());
            case Integer i -> Integer.compare(i, upper.intValue());
            case Float f -> Float.compare(f, upper.floatValue());
            case Double d -> Double.compare(d, upper.doubleValue());
            case Long l -> Long.compare(l, upper.longValue());
            default -> null;
        };

        if (cmpUpper == null) return false;

        if (lower != null) {
            var cmpLower = switch (actual) {
                case Byte b -> Byte.compare(b, lower.byteValue());
                case Integer i -> Integer.compare(i, lower.intValue());
                case Float f -> Float.compare(f, lower.floatValue());
                case Double d -> Double.compare(d, lower.doubleValue());
                case Long l -> Long.compare(l, lower.longValue());
                default -> null;
            };

            return switch (this) {
                case EQUALS -> cmpUpper == 0;
                case NOT_EQUALS -> cmpUpper != 0;
                case GREATER_THAN -> cmpUpper > 0;
                case GREATER_EQUAL -> cmpUpper >= 0;
                case LESS_THAN -> cmpUpper < 0;
                case LESS_EQUAL -> cmpUpper <= 0;
                case IN_RANGE -> cmpLower > 0 && cmpUpper < 0;
                case INCLUSIVE_LOWER_RANGE -> cmpLower >= 0 && cmpUpper < 0;
                case INCLUSIVE_UPPER_RANGE -> cmpLower > 0 && cmpUpper <= 0;
                case INCLUSIVE_RANGE -> cmpLower >= 0 && cmpUpper <= 0;
            };
        }

        return switch (this) {
            case EQUALS -> cmpUpper == 0;
            case NOT_EQUALS -> cmpUpper != 0;
            case GREATER_THAN -> cmpUpper > 0;
            case GREATER_EQUAL -> cmpUpper >= 0;
            case LESS_THAN -> cmpUpper < 0;
            case LESS_EQUAL -> cmpUpper <= 0;
            case IN_RANGE,
                 INCLUSIVE_LOWER_RANGE,
                 INCLUSIVE_UPPER_RANGE,
                 INCLUSIVE_RANGE-> false;
        };
    }
}
