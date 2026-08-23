package sleys.efedp.system.animations.json.properties.functional.datapackets.write;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.sl.library.SLLibrary;

public sealed interface IDataWriter permits WriteArithmeticData, WriteLogicalData, WriteStringData {
    DataPacketType type();
    String dataId();
    void resolve(Player player);

    default boolean isValid(Player player) {
        if (player == null) {
            SLLibrary.LOGGER.warn(
                    "An attempt was made to send data through the Sync Sender; However,the operation cannot proceed since the signature is null."
            );
            return false;
        }
        return true;
    }

    default boolean isntValid(Player player) {
        return !this.isValid(player);
    }


    MapCodec<IDataWriter> CODEC = DataPacketType.CODEC.dispatchMap(
            "type", IDataWriter::type,
            DataPacketType::writeCodec
    );
}
