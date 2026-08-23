package sleys.efedp.system.animations.json.properties.functional.datapackets.instruction;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.sl.library.network.sync.TagSyncSender;
import sleys.sl.library.util.data.codec.EnumCodecs;

import javax.annotation.Nullable;
import java.util.function.Consumer;

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
