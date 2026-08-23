package sleys.efedp.system.animations.json.properties.functional.datapackets.comparator;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum NumericComparator {
    EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_EQUAL, LESS_THAN, LESS_EQUAL;
    public static final Codec<NumericComparator> CODEC = EnumCodecs.byId(values(), Enum::name);
}
