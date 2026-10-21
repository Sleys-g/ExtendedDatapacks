package sleys.efedp.neoforge.core.datapackets.get;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.neoforge.core.datapackets.DataPacketType;

public record GetStringData(DataPacketType type, String dataId) implements IDataGet {

    public static MapCodec<GetStringData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(GetStringData::dataId)
        ).apply(instance, id-> new GetStringData(type, id)));
    }

    @Override
    public String readData(LivingEntity livingEntity) {
        var tag = livingEntity.getPersistentData();
        return (String) type.read(tag, dataId);
    }
}