package sleys.efedp.system.animations.json.properties.functional.datapackets.write;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.efedp.system.animations.json.properties.functional.datapackets.instruction.StringInstruction;
import sleys.sl.library.network.sync.TagSyncSender;

public record WriteStringData(DataPacketType type, String dataId,
                              StringInstruction instruction, String value) implements IDataWriter {

    public static MapCodec<WriteStringData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(WriteStringData::dataId),
                StringInstruction.CODEC.optionalFieldOf("instruction", StringInstruction.SET)
                        .forGetter(WriteStringData::instruction),
                type.objectCodec().fieldOf("value").forGetter(WriteStringData::value)
        ).apply(instance, (id, op, value) ->
                new WriteStringData(type, id, op, (String) value)
        ));
    }

    @Override
    public void resolve(Player player) {
        if (this.isntValid(player)) return;
        var level = player.level();

        var currentValue = player.getPersistentData().getString(dataId);
        var outputValue = instruction.apply(currentValue, value);

        if (level.isClientSide) type.write(TagSyncSender.SyncMethod.SAVE_AND_CTS, player, dataId, outputValue);
        if (!level.isClientSide) type.write(TagSyncSender.SyncMethod.SAVE_AND_STC, player, dataId, outputValue);
    }
}

