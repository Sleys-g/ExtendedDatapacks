package sleys.efedp.system.innates.json;

import java.util.Locale;

public enum ConditionsType {
    NORMAL, SPRINTING, KNEELING, IN_AIR, USE_ITEM;

    public static ConditionsType fromString(String str) {
        return switch (str.toLowerCase(Locale.ROOT)) {
            case "sprinting" -> SPRINTING;
            case "kneeling" -> KNEELING;
            case "in_air" -> IN_AIR;
            case "use_item" -> USE_ITEM;
            default -> NORMAL;
        };
    }
}