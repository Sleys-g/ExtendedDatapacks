package sleys.efedp.system.animations.json.properties.functional.datapackets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import sleys.efedp.system.animations.json.properties.functional.datapackets.read.IDataRead;

import java.util.List;

public record ReadDataPacketsGroup(List<IDataRead> and, List<IDataRead> or, List<IDataRead> xor, List<IDataRead> not) {

    public static final Codec<ReadDataPacketsGroup> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    IDataRead.CODEC.codec().listOf().optionalFieldOf("AND", List.of())
                            .forGetter(ReadDataPacketsGroup::and),
                    IDataRead.CODEC.codec().listOf().optionalFieldOf("OR", List.of())
                            .forGetter(ReadDataPacketsGroup::or),
                    IDataRead.CODEC.codec().listOf().optionalFieldOf("XOR", List.of())
                            .forGetter(ReadDataPacketsGroup::xor),
                    IDataRead.CODEC.codec().listOf().optionalFieldOf("NOT", List.of())
                            .forGetter(ReadDataPacketsGroup::not)
            ).apply(instance, ReadDataPacketsGroup::new));

    public boolean syncedEvaluate(Player player) {
        boolean andOk = and.stream().allMatch(r -> r.readSyncData(player));
        boolean orOk  = or.isEmpty()  || or.stream().anyMatch(r -> r.readSyncData(player));
        boolean xorOk = xor.isEmpty() || xor.stream().filter(r -> r.readSyncData(player)).count() == 1;
        boolean notOk = not.stream().noneMatch(r -> r.readSyncData(player));
        return andOk && orOk && xorOk && notOk;
    }

    public boolean evaluate(LivingEntity livingEntity) {
        boolean andOk = and.stream().allMatch(r -> r.readData(livingEntity));
        boolean orOk  = or.isEmpty()  || or.stream().anyMatch(r -> r.readData(livingEntity));
        boolean xorOk = xor.isEmpty() || xor.stream().filter(r -> r.readData(livingEntity)).count() == 1;
        boolean notOk = not.stream().noneMatch(r -> r.readData(livingEntity));
        return andOk && orOk && xorOk && notOk;
    }
}