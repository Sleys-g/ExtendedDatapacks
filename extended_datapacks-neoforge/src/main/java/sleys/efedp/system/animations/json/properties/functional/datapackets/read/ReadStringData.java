package sleys.efedp.system.animations.json.properties.functional.datapackets.read;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.efedp.system.animations.json.properties.functional.datapackets.comparator.StringComparator;
import sleys.sl.library.network.sync.TagSyncSender;

public record ReadStringData(DataPacketType type, String dataId,
                             StringComparator comparator,
                             String expected, boolean delete) implements IDataRead {

    public static MapCodec<ReadStringData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(ReadStringData::dataId),
                StringComparator.CODEC.fieldOf("comparator").forGetter(ReadStringData::comparator),
                type.objectCodec().fieldOf("expected").forGetter(ReadStringData::expected),
                Codec.BOOL.optionalFieldOf("delete", false).forGetter(ReadStringData::delete)
        ).apply(instance, (id, cmp, expected, delete) ->
                new ReadStringData(type, id, cmp, (String) expected, delete)));
    }

    @Override
    public boolean readSyncData(Player player) {
        if (this.isntValid(player)) return false;
        var tag = player.getPersistentData();

        String actual = (String) type.read(tag, dataId);
        boolean result = switch (comparator) {
            case EQUALS -> actual.equals(expected);
            case CONTAINS -> actual.contains(expected);
        };

        if (result && delete) TagSyncSender.removeSender(player, dataId);
        return result;
    }

    @Override
    public boolean readData(LivingEntity livingEntity) {
        if (this.isntValid(livingEntity)) return false;
        var tag = livingEntity.getPersistentData();

        String actual = (String) type.read(tag, dataId);
        boolean result = switch (comparator) {
            case EQUALS -> actual.equals(expected);
            case CONTAINS -> actual.contains(expected);
        };

        if (result && delete) tag.remove(dataId);
        return result;
    }
}
