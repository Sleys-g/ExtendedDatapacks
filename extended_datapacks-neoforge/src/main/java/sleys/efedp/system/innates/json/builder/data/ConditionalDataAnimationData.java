package sleys.efedp.system.innates.json.builder.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.properties.functional.datapackets.ReadDataPacketsGroup;
import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;

import java.util.List;
import java.util.Optional;

public record ConditionalDataAnimationData(String animation,
                                           ConditionalType physicalCondition,
                                           Optional<ReadDataPacketsGroup> readData,
                                           Optional<String> tooltipHead,
                                           List<InnatePhaseProperties> properties) {

    public static final MapCodec<ConditionalDataAnimationData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("animation").forGetter(ConditionalDataAnimationData::animation),
                    ConditionalType.CODEC.fieldOf("physical_condition")
                            .forGetter(ConditionalDataAnimationData::physicalCondition),
                    ReadDataPacketsGroup.CODEC.optionalFieldOf("read_data")
                            .forGetter(ConditionalDataAnimationData::readData),
                    Codec.STRING.optionalFieldOf("tooltip_head")
                            .forGetter(ConditionalDataAnimationData::tooltipHead),
                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties)
            ).apply(instance, ConditionalDataAnimationData::new)
    );
}
