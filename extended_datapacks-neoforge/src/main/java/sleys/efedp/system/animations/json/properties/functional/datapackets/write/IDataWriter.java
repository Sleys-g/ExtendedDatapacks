package sleys.efedp.system.animations.json.properties.functional.datapackets.write;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;

public sealed interface IDataWriter permits WriteArithmeticData, WriteLogicalData, WriteStringData {
    DataPacketType type();
    String dataId();
    void writeSyncData(Player player);
    void writeData(LivingEntity livingEntity);

    default boolean isValid(LivingEntity livingEntity) {
        if (livingEntity == null) {
            ExtendedDatapacks.LOGGER.warn(
                    "An attempt was made to send data through the Sync Sender; However,the operation cannot proceed since the signature is null."
            );
            return false;
        }
        return true;
    }

    default boolean isntValid(LivingEntity livingEntity) {
        return !this.isValid(livingEntity);
    }


    MapCodec<IDataWriter> CODEC = DataPacketType.CODEC.dispatchMap(
            "type", IDataWriter::type,
            DataPacketType::writeCodec
    );
}
