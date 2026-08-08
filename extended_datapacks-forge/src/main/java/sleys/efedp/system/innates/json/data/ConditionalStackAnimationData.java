package sleys.efedp.system.innates.json.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;

import java.util.List;

public record ConditionalStackAnimationData(String animation, Integer stack, List<InnatePhaseProperties> properties) {

    public static final MapCodec<ConditionalStackAnimationData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("animation").forGetter(ConditionalStackAnimationData::animation),
                    Codec.INT.fieldOf("stacks").forGetter(ConditionalStackAnimationData::stack),
                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties)
            ).apply(instance, ConditionalStackAnimationData::new)
    );
}
