package sleys.efedp.system.animations.json.properties.functional.datapackets.read;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.efedp.system.animations.json.properties.functional.datapackets.comparator.NumericComparator;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.network.sync.TagSyncSender;

import java.util.Optional;

public record ReadArithmeticData(DataPacketType type, String dataId,
                                 NumericComparator comparator,
                                 Number value, Number max, Number min, boolean delete) implements IDataRead {

    @SuppressWarnings("all")
    public static MapCodec<ReadArithmeticData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId")
                        .forGetter(ReadArithmeticData::dataId),

                NumericComparator.CODEC.fieldOf("comparator")
                        .forGetter(ReadArithmeticData::comparator),

                type.objectCodec()
                        .optionalFieldOf("value")
                        .forGetter(data -> Optional.ofNullable((Object) data.value)),

                type.objectCodec()
                        .optionalFieldOf("max")
                        .forGetter(data -> Optional.ofNullable((Object) data.max)),

                type.objectCodec()
                        .optionalFieldOf("min")
                        .forGetter(data -> Optional.ofNullable((Object) data.min)),

                Codec.BOOL.optionalFieldOf("delete", false)
                        .forGetter(ReadArithmeticData::delete)

        ).apply(instance, (id, cmp, value, max, min, delete) ->
                new ReadArithmeticData(
                        type, id, cmp,
                        (Number) value.orElse(null),
                        (Number) max.orElse(null),
                        (Number) min.orElse(null),
                        delete
                ))
        );
    }

    @Override
    public boolean readSyncData(Player player) {
        if (this.isntValid(player)) return false;
        var tag = player.getPersistentData();
        var result = this.getResult(tag);
        if (result && delete) TagSyncSender.removeSender(player, dataId);
        return result;
    }

    @Override
    public boolean readData(LivingEntity livingEntity) {
        if (this.isntValid(livingEntity)) return false;
        var tag = livingEntity.getPersistentData();
        var result = this.getResult(tag);
        if (result && delete) tag.remove(dataId);
        return result;
    }

    private boolean getResult(CompoundTag tag) {
        Object actual = type.read(tag, dataId);

        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                () -> value != null
                        ? comparator.comparate(actual, value)
                        : comparator.comparate(actual, max, min),
                false
        );
    }
}