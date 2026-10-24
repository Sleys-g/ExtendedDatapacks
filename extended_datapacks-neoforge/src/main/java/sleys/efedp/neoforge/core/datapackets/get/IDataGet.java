package sleys.efedp.neoforge.core.datapackets.get;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.core.datapackets.DataPacketType;

public interface IDataGet {
    DataPacketType type();
    String dataId();

    Object readData(LivingEntity livingEntity);

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

    MapCodec<IDataGet> CODEC = DataPacketType.CODEC.dispatchMap(
            "type", IDataGet::type, DataPacketType::getCodec
    );
}
