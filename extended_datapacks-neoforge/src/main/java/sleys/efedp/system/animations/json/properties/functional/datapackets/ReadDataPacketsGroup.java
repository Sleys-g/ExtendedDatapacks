package sleys.efedp.system.animations.json.properties.functional.datapackets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public boolean evaluate(Player player) {
        boolean andOk = and.stream().allMatch(r -> r.evaluate(player));
        boolean orOk  = or.isEmpty()  || or.stream().anyMatch(r -> r.evaluate(player));
        boolean xorOk = xor.isEmpty() || xor.stream().filter(r -> r.evaluate(player)).count() == 1;
        boolean notOk = not.stream().noneMatch(r -> r.evaluate(player));
        return andOk && orOk && xorOk && notOk;
    }
}