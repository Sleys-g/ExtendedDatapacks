package sleys.efedp.neoforge.core.datapackets.get;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.neoforge.core.datapackets.DataPacketType;

public record GetLogicalData(DataPacketType type, String dataId) implements IDataGet {

    public static MapCodec<GetLogicalData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(GetLogicalData::dataId)
        ).apply(instance, id-> new GetLogicalData(type, id)));
    }

    @Override
    public Boolean readData(LivingEntity livingEntity) {
        var tag = livingEntity.getPersistentData();
        return (Boolean) type.read(tag, dataId);
    }
}