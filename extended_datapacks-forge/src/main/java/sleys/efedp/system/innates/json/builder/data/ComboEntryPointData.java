package sleys.efedp.system.innates.json.builder.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ComboEntryPointData(String node, Boolean global, ConditionalType physicalCondition) {
    public static final Codec<ComboEntryPointData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("node").forGetter(ComboEntryPointData::node),
                    Codec.BOOL.optionalFieldOf("global", false).forGetter(ComboEntryPointData::global),
                    ConditionalType.CODEC.fieldOf("physical_condition").forGetter(ComboEntryPointData::physicalCondition)
            ).apply(instance, ComboEntryPointData::new));
}
