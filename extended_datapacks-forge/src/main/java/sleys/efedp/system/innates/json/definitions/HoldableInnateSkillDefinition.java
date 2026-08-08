package sleys.efedp.system.innates.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.innates.json.builder.wrapper.holdable.WHoldableInnateSkill;
import sleys.efedp.system.innates.json.data.HoldableSkillValues;
import sleys.efedp.system.innates.json.data.ListenerSkillValues;
import sleys.efedp.system.innates.json.properties.InnatePhaseProperties;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.List;

public record HoldableInnateSkillDefinition(
        String name, String animation, String chargeAnimation,
        boolean disableTooltipProperties,
        int maxAllowedCharging, int maxChargingTicks, int minChargingTicks,
        boolean reduceSpeed, List<InnatePhaseProperties> properties, List<JsonComponentArgs> tooltip
) implements IInnateSkillDefinition<WHoldableInnateSkill> {

    public static final MapCodec<HoldableInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(HoldableInnateSkillDefinition::name),
                    Codec.STRING.fieldOf("animation").forGetter(HoldableInnateSkillDefinition::animation),
                    Codec.STRING.fieldOf("chargeAnimation").forGetter(HoldableInnateSkillDefinition::chargeAnimation),

                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(HoldableInnateSkillDefinition::disableTooltipProperties),

                    Codec.INT.fieldOf("maxAllowedCharging").forGetter(HoldableInnateSkillDefinition::maxAllowedCharging),
                    Codec.INT.fieldOf("maxChargingTicks").forGetter(HoldableInnateSkillDefinition::maxChargingTicks),
                    Codec.INT.fieldOf("minChargingTicks").forGetter(HoldableInnateSkillDefinition::minChargingTicks),

                    Codec.BOOL.fieldOf("reduceSpeed").forGetter(HoldableInnateSkillDefinition::reduceSpeed),

                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties),

                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip)

            ).apply(instance, HoldableInnateSkillDefinition::new
            )
    );

    public WHoldableInnateSkill.Builder createBuilder(
            String modId,
            AnimationManager.AnimationAccessor<? extends StaticAnimation> animationAccessor,
            AnimationManager.AnimationAccessor<? extends StaticAnimation> chargingAnimationAccessor) {

        var holdableValues = new HoldableSkillValues(maxAllowedCharging, maxChargingTicks, minChargingTicks, reduceSpeed);
        var listenerValues = new ListenerSkillValues(modId, ResourceLocation.fromNamespaceAndPath(modId, name));
        return WHoldableInnateSkill
                .createHoldableInnateSkillBuilder()
                .setHoldableValues(holdableValues)
                .setListenerValues(listenerValues)
                .setAnimation(animationAccessor)
                .setChargingAnimation(chargingAnimationAccessor)
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties);
    }

    @Override
    public void applyProperties(WHoldableInnateSkill builder) {
        properties.forEach(property -> property.applyTo(builder));
    }
}
