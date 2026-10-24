package sleys.efedp.forge.core.datapackets.get;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.forge.core.datapackets.DataPacketType;

public record GetArithmeticData(DataPacketType type, String dataId) implements IDataGet {

    public static MapCodec<GetArithmeticData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("data_id").forGetter(GetArithmeticData::dataId)
        ).apply(instance, id -> new GetArithmeticData(type, id)));
    }

    @Override
    public Number readData(LivingEntity livingEntity) {
        if (isntValid(livingEntity)) return 0;
        var tag = livingEntity.getPersistentData();

        Object val = type.read(tag, dataId);
        return val instanceof Number num ? num : 0;
    }
}