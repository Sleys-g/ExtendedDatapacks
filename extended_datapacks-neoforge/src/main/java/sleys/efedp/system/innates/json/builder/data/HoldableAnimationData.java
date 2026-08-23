package sleys.efedp.system.innates.json.builder.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.innates.json.definitions.HoldableConditionalInnateSkillDefinition;
import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;

import java.util.List;

public record HoldableAnimationData(String animation, boolean playbackForRelease, List<InnatePhaseProperties> properties) {

    public static final MapCodec<HoldableAnimationData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("animation").forGetter(HoldableAnimationData::animation),
                    Codec.BOOL.optionalFieldOf("playbackForRelease", false)
                            .forGetter(HoldableAnimationData::playbackForRelease),
                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties)
            ).apply(instance, HoldableAnimationData::new)
    );
}
