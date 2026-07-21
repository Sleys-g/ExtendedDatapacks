package sleys.efedp.conditions;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public enum ComparisonType {
    EQUAL("="),
    NOT_EQUAL("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN_OR_EQUAL("<=");

    private final String symbol;

    ComparisonType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static ComparisonType fromString(String value) {
        for (ComparisonType type : values()) {
            if (type.symbol.equals(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return EQUAL;
    }
}