package sleys.efedp.system.animations.json.properties.functional.datapackets.write;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.efedp.system.animations.json.properties.functional.datapackets.instruction.ArithmeticInstruction;
import sleys.sl.library.network.sync.TagSyncSender;

public record WriteArithmeticData(DataPacketType type, String dataId,
                                  ArithmeticInstruction instruction, Number value) implements IDataWriter {

    public static MapCodec<WriteArithmeticData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(WriteArithmeticData::dataId),
                ArithmeticInstruction.CODEC.optionalFieldOf("instruction", ArithmeticInstruction.SET)
                        .forGetter(WriteArithmeticData::instruction),
                type.objectCodec().fieldOf("value").forGetter(WriteArithmeticData::value)
        ).apply(instance, (id, op, value) ->
                new WriteArithmeticData(type, id, op, (Number) value)
        ));
    }

    @Override
    public void writeSyncData(Player player) {
        if (this.isntValid(player)) return;
        var level = player.level();
        var data = player.getPersistentData();

        if (value instanceof Byte b) this.sendSenderSync(level, player, instruction.apply(data.getByte(dataId), b, dataId));
        else if (value instanceof Integer i) this.sendSenderSync(level, player, instruction.apply(data.getInt(dataId), i, dataId));
        else if (value instanceof Float f) this.sendSenderSync(level, player, instruction.apply(data.getFloat(dataId), f, dataId));
        else if (value instanceof Double d) this.sendSenderSync(level, player, instruction.apply(data.getDouble(dataId), d, dataId));
        else if (value instanceof Long l)  this.sendSenderSync(level, player, instruction.apply(data.getLong(dataId), l, dataId));
    }

    private void sendSenderSync(Level level, Player player, Object value) {
        if (level.isClientSide) type.write(TagSyncSender.SyncMethod.SAVE_AND_CTS, player, dataId, value);
        if (!level.isClientSide) type.write(TagSyncSender.SyncMethod.SAVE_AND_STC, player, dataId, value);
    }

    @Override
    public void writeData(LivingEntity livingEntity) {
        if (this.isntValid(livingEntity)) return;
        var data = livingEntity.getPersistentData();

        if (value instanceof Byte b) data.putByte(dataId, instruction.apply(data.getByte(dataId), b, dataId));
        else if (value instanceof Integer i) data.putInt(dataId, instruction.apply(data.getInt(dataId), i, dataId));
        else if (value instanceof Float f) data.putFloat(dataId, instruction.apply(data.getFloat(dataId), f, dataId));
        else if (value instanceof Double d) data.putDouble(dataId, instruction.apply(data.getDouble(dataId), d, dataId));
        else if (value instanceof Long l) data.putLong(dataId, instruction.apply(data.getLong(dataId), l, dataId));
    }
}
