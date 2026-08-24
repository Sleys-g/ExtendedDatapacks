package sleys.efedp.system.animations.json.properties.functional.datapackets.read;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import sleys.efedp.system.animations.json.properties.functional.datapackets.DataPacketType;
import sleys.sl.library.functional.TriConsumer;
import sleys.sl.library.network.sync.CTSRemoveTagSyncPacket;
import sleys.sl.library.network.sync.TagSyncSender;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record ReadLogicalData(DataPacketType type, String dataId,
                              Boolean expected, boolean delete) implements IDataRead {

    public static MapCodec<ReadLogicalData> codecFor(DataPacketType type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("dataId").forGetter(ReadLogicalData::dataId),
                type.objectCodec().fieldOf("expected").forGetter(ReadLogicalData::expected),
                Codec.BOOL.optionalFieldOf("delete", false).forGetter(ReadLogicalData::delete)
        ).apply(instance, (id, expected, delete) ->
                new ReadLogicalData(type, id, (Boolean) expected, delete)));
    }

    @Override
    public boolean readSyncData(Player player) {
        if (this.isntValid(player)) return false;
        var tag = player.getPersistentData();

        boolean actual = tag.getBoolean(dataId);
        boolean result = actual == expected;

        if (result && delete) TagSyncSender.removeSender(player, dataId);
        return result;
    }

    @Override
    public boolean readData(LivingEntity livingEntity) {
        if (this.isntValid(livingEntity)) return false;
        var tag = livingEntity.getPersistentData();

        boolean actual = tag.getBoolean(dataId);
        boolean result = actual == expected;

        if (result && delete) tag.remove(dataId);
        return result;
    }
}
