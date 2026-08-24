package sleys.efedp.system.animations.json.properties.functional.datapackets.read;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.efedp.system.animations.json.properties.functional.datapackets.comparator.NumericComparator;
import sleys.sl.library.network.sync.TagSyncSender;

public record ReadArithmeticData(DataPacketType type, String dataId,
                                 NumericComparator comparator,
                                 Number expected, boolean delete) implements IDataRead {

    public static MapCodec<ReadArithmeticData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(ReadArithmeticData::dataId),
                NumericComparator.CODEC.fieldOf("comparator").forGetter(ReadArithmeticData::comparator),
                type.objectCodec().fieldOf("expected").forGetter(ReadArithmeticData::expected),
                Codec.BOOL.optionalFieldOf("delete", false).forGetter(ReadArithmeticData::delete)
        ).apply(instance, (id, cmp, expected, delete) ->
                new ReadArithmeticData(type, id, cmp, (Number) expected, delete)));
    }

    @Override
    public boolean readSyncData(Player player) {
        if (this.isntValid(player)) return false;
        var tag = player.getPersistentData();

        Object actual = type.read(tag, dataId);
        Integer cmp = switch (actual) {
            case Byte b -> Byte.compare(b, expected.byteValue());
            case Integer i -> Integer.compare(i, expected.intValue());
            case Float f -> Float.compare(f, expected.floatValue());
            case Double d -> Double.compare(d, expected.doubleValue());
            case Long l -> Long.compare(l, expected.longValue());
            default -> null;
        };

        if (cmp == null) return false;
        var result = switch (comparator) {
            case EQUALS -> cmp == 0;
            case NOT_EQUALS -> cmp != 0;
            case GREATER_THAN -> cmp > 0;
            case GREATER_EQUAL -> cmp >= 0;
            case LESS_THAN -> cmp < 0;
            case LESS_EQUAL -> cmp <= 0;
        };

        if (result && delete) TagSyncSender.removeSender(player, dataId);
        return result;
    }

    @Override
    public boolean readData(LivingEntity livingEntity) {
        if (this.isntValid(livingEntity)) return false;
        var tag = livingEntity.getPersistentData();

        Object actual = type.read(tag, dataId);
        Integer cmp = switch (actual) {
            case Byte b -> Byte.compare(b, expected.byteValue());
            case Integer i -> Integer.compare(i, expected.intValue());
            case Float f -> Float.compare(f, expected.floatValue());
            case Double d -> Double.compare(d, expected.doubleValue());
            case Long l -> Long.compare(l, expected.longValue());
            default -> null;
        };

        if (cmp == null) return false;
        var result = switch (comparator) {
            case EQUALS -> cmp == 0;
            case NOT_EQUALS -> cmp != 0;
            case GREATER_THAN -> cmp > 0;
            case GREATER_EQUAL -> cmp >= 0;
            case LESS_THAN -> cmp < 0;
            case LESS_EQUAL -> cmp <= 0;
        };

        if (result && delete) tag.remove(dataId);
        return result;
    }
}