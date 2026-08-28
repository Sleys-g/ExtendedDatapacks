package sleys.efedp.system.innates.json.builder.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;

import java.util.List;

public record AnimationData(String animation, List<InnatePhaseProperties> properties) {

    public static final MapCodec<AnimationData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("animation").forGetter(AnimationData::animation),
                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties)
            ).apply(instance, AnimationData::new)
    );
}
