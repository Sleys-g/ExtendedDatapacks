package sleys.efedp.system.innates.json.builder.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record ComboTransitionData(String node, Optional<ConditionalType> physicalCondition) {

    public static final Codec<ComboTransitionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("node")
                            .forGetter(ComboTransitionData::node),
                    ConditionalType.CODEC.optionalFieldOf("physical_condition")
                            .forGetter(ComboTransitionData::physicalCondition)
            ).apply(instance, ComboTransitionData::new));
}
