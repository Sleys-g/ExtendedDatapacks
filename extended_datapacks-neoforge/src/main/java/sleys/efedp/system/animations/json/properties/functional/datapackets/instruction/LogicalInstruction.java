package sleys.efedp.system.animations.json.properties.functional.datapackets.instruction;

import com.mojang.serialization.Codec;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum LogicalInstruction {
    SET, TOGGLE, AND, OR, XOR;
    public static final Codec<LogicalInstruction> CODEC = EnumCodecs.byId(values(), Enum::name);

    public boolean apply(boolean current, Boolean operand) {
        return switch (this) {
            case SET -> operand;
            case TOGGLE -> !current;
            case AND -> current && operand;
            case OR -> current || operand;
            case XOR -> current ^ operand;
        };
    }
}
