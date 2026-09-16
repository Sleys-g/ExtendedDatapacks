package sleys.efedp.forge.system.animations.json.properties.datapackets.comparator;

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
        if (upper == null) return false;

        var cmpUpper = this.getCmp(actual, upper);
        if (cmpUpper == null) return false;

        if (lower != null) {

            var cmpLower = this.getCmp(actual, lower);
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

    private <N extends Number> Integer getCmp(Object value, N compared) {
        Integer cmpUpper = null;

        if (value instanceof Byte b) cmpUpper = Byte.compare(b, compared.byteValue());
        else if (value instanceof Short s) cmpUpper = Short.compare(s, compared.shortValue());
        else if (value instanceof Integer i) cmpUpper = Integer.compare(i, compared.intValue());
        else if (value instanceof Float f) cmpUpper = Float.compare(f, compared.floatValue());
        else if (value instanceof Double d) cmpUpper = Double.compare(d, compared.doubleValue());
        else if (value instanceof Long l) cmpUpper = Long.compare(l, compared.longValue());

        return cmpUpper;
    }
}
