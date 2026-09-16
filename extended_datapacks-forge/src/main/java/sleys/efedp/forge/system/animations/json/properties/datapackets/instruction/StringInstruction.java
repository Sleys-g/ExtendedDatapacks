package sleys.efedp.forge.system.animations.json.properties.datapackets.instruction;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum StringInstruction {
    SET, APPEND, PREPEND;
    public static final Codec<StringInstruction> CODEC = EnumCodecs.byId(values(), Enum::name);

    public String apply(String current, String operand) {
        return switch (this) {
            case SET -> operand;
            case APPEND -> current + operand;
            case PREPEND -> operand + current;
        };
    }
}
