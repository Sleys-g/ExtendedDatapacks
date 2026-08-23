package sleys.efedp.system.innates.json.builder.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;

import java.util.List;

public record ComboNodeData(String animation, List<InnatePhaseProperties> properties, List<ComboTransitionData> next) {

    public static final MapCodec<ComboNodeData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("animation").forGetter(ComboNodeData::animation),
                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties),
                    ComboTransitionData.CODEC.listOf().optionalFieldOf("next", List.of())
                            .forGetter(ComboNodeData::next)
            ).apply(instance, ComboNodeData::new));
}
