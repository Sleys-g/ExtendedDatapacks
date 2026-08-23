package sleys.efedp.system.animations.json.properties.functional.datapackets.read;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;

public record ReadLogicalData(DataPacketType type, String dataId, Boolean expected, boolean delete)
        implements IDataRead {

    public static MapCodec<ReadLogicalData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(ReadLogicalData::dataId),
                type.objectCodec().fieldOf("expected").forGetter(ReadLogicalData::expected),
                Codec.BOOL.optionalFieldOf("delete", false).forGetter(ReadLogicalData::delete)
        ).apply(instance, (id, expected, delete) ->
                new ReadLogicalData(type, id, (Boolean) expected, delete)));
    }

    @Override
    public boolean evaluate(Player player) {
        if (this.isntValid(player)) return false;
        var tag = player.getPersistentData();

        boolean actual = tag.getBoolean(dataId);
        boolean result = actual == expected;
        if (delete) tag.remove(dataId);
        return result;
    }
}
