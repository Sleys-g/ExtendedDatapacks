package sleys.efedp.system.animations.json.properties.functional.datapackets.comparator;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum StringComparator {
    EQUALS, CONTAINS;
    public static final Codec<StringComparator> CODEC = EnumCodecs.byId(values(), Enum::name);
}