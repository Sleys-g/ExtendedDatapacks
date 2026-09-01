package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.innates.json.builder.data.AnimationData;
import sleys.efedp.system.innates.json.builder.data.ConditionalType;
import sleys.efedp.system.innates.json.builder.data.HoldableAnimationData;
import sleys.efedp.system.innates.json.builder.values.HoldableSkillValues;
import sleys.efedp.system.innates.json.builder.values.ListenerSkillValues;
import sleys.efedp.system.innates.json.builder.wrapper.holdable.WHoldableConditionalInnateSkill;
import sleys.efedp.system.innates.json.builder.wrapper.holdable.WHoldableInnateSkill;
import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.List;
import java.util.Map;

public record HoldableConditionalInnateSkillDefinition(
        String name, String chargeAnimation,
        boolean disableTooltipProperties,
        int maxAllowedCharging, int maxChargingTicks, int minChargingTicks,
        boolean reduceSpeed, boolean playbackForCharging,
        List<JsonComponentArgs> tooltip,
        Map<ConditionalType, HoldableAnimationData> conditionalAnimationData
) implements IInnateSkillDefinition<WHoldableConditionalInnateSkill.Builder> {

    public static final MapCodec<HoldableConditionalInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(HoldableConditionalInnateSkillDefinition::name),
                    Codec.STRING.fieldOf("chargeAnimation").forGetter(HoldableConditionalInnateSkillDefinition::chargeAnimation),

                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(HoldableConditionalInnateSkillDefinition::disableTooltipProperties),

                    Codec.INT.fieldOf("maxAllowedCharging").forGetter(HoldableConditionalInnateSkillDefinition::maxAllowedCharging),
                    Codec.INT.fieldOf("maxChargingTicks").forGetter(HoldableConditionalInnateSkillDefinition::maxChargingTicks),
                    Codec.INT.fieldOf("minChargingTicks").forGetter(HoldableConditionalInnateSkillDefinition::minChargingTicks),
                    Codec.BOOL.fieldOf("reduceSpeed").forGetter(HoldableConditionalInnateSkillDefinition::reduceSpeed),

                    Codec.BOOL.optionalFieldOf("playbackForCharging", false)
                            .forGetter(HoldableConditionalInnateSkillDefinition::playbackForCharging),
                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip),

                    Codec.unboundedMap(ConditionalType.CODEC, HoldableAnimationData.CODEC.codec())
                            .fieldOf("holdable_conditions")
                            .forGetter(HoldableConditionalInnateSkillDefinition::conditionalAnimationData)

            ).apply(instance, HoldableConditionalInnateSkillDefinition::new)
    );

    public WHoldableConditionalInnateSkill.Builder createBuilder(
            String modId, AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimationAccessor) {

        var holdableValues = new HoldableSkillValues(
                playbackForCharging, false, /// !
                maxAllowedCharging, maxChargingTicks,
                minChargingTicks, reduceSpeed
        );
        var listenerValues = new ListenerSkillValues(modId, ResourceLocation.fromNamespaceAndPath(modId, name));
        return WHoldableConditionalInnateSkill
                .createHoldableConditionalInnateSkillBuilder(WHoldableConditionalInnateSkill::new)
                .setHoldableValues(holdableValues)
                .setListenerValues(listenerValues)
                .setChargingAnimation(chargingAnimationAccessor)
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties);
    }
}
