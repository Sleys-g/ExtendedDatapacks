package sleys.efedp.system.animations.json.properties.functional.datapackets.write;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.efedp.system.animations.json.properties.functional.datapackets.instruction.LogicalInstruction;
import sleys.sl.library.network.sync.TagSyncSender;

public record WriteLogicalData(DataPacketType type, String dataId,
                               LogicalInstruction instruction, Boolean value) implements IDataWriter {

    public static MapCodec<WriteLogicalData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(WriteLogicalData::dataId),
                LogicalInstruction.CODEC.optionalFieldOf("instruction", LogicalInstruction.SET)
                        .forGetter(WriteLogicalData::instruction),
                type.objectCodec().optionalFieldOf("value", Boolean.FALSE).forGetter(WriteLogicalData::value)
        ).apply(instance, (id, op, value) ->
                new WriteLogicalData(type, id, op, (Boolean) value)
        ));
    }

    @Override
    public void resolve(Player player) {
        if (this.isntValid(player)) return;
        var level = player.level();

        var currentValue = player.getPersistentData().getBoolean(dataId);
        var outputValue = instruction.apply(currentValue, value);

        if (level.isClientSide) type.write(TagSyncSender.SyncMethod.SAVE_AND_CTS, player, dataId, outputValue);
        if (!level.isClientSide) type.write(TagSyncSender.SyncMethod.SAVE_AND_STC, player, dataId, outputValue);
    }
}
