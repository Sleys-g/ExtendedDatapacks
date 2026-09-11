package sleys.efedp.system.animations.json.properties.functional.datapackets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.properties.functional.datapackets.write.IDataWriter;

import java.util.List;

public record WriteDataPacketsDefinitions(List<IDataWriter> writers) {

    public static final Codec<WriteDataPacketsDefinitions> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(
                    IDataWriter.CODEC.codec().listOf().optionalFieldOf("write_data", List.of())
                            .forGetter(WriteDataPacketsDefinitions::writers)
            ).apply(instance, WriteDataPacketsDefinitions::new));
}
