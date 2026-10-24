package sleys.efedp.forge.system.skills.weapon.innate.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.forge.core.control.controlable.ControlableEvents;
import sleys.efedp.forge.system.skills.weapon.innate.properties.InnatePhaseProperties;
import sleys.efedp.forge.system.skills.weapon.innate.values.ListenerSkillValues;
import sleys.efedp.forge.system.skills.weapon.innate.wrapper.timed.WTimedAdvancedInnateSkill;
import sleys.sl.library.util.io.JsonComponentArgs;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillCategories;

import java.util.List;
import java.util.Optional;

public record TimedAdvancedInnateSkillDefinition(
        String name, String animation,
        Optional<ResourceLocation> soundEvent,
        boolean disableTooltipProperties,
        List<InnatePhaseProperties> properties,
        List<JsonComponentArgs> tooltip,
        ControlableEvents controlableEvents
) implements IInnateSkillDefinition<WTimedAdvancedInnateSkill> {

    public static final MapCodec<TimedAdvancedInnateSkillDefinition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(TimedAdvancedInnateSkillDefinition::name),
                    Codec.STRING.fieldOf("animation").forGetter(TimedAdvancedInnateSkillDefinition::animation),
                    ResourceLocation.CODEC.optionalFieldOf("sound").forGetter(TimedAdvancedInnateSkillDefinition::soundEvent),

                    Codec.BOOL.optionalFieldOf("disableTooltipProperties", false)
                            .forGetter(TimedAdvancedInnateSkillDefinition::disableTooltipProperties),

                    InnatePhaseProperties.CODEC.codec()
                            .listOf()
                            .optionalFieldOf("properties", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.properties),

                    JsonComponentArgs.CODEC
                            .listOf()
                            .optionalFieldOf("tooltip", List.of())
                            .forGetter(skillDefinitions -> skillDefinitions.tooltip),

                    ControlableEvents.CODEC.optionalFieldOf("controlable_events", ControlableEvents.EMPTY)
                            .forGetter(TimedAdvancedInnateSkillDefinition::controlableEvents)
                    ).apply(instance, TimedAdvancedInnateSkillDefinition::new
            )
    );

    @SuppressWarnings("deprecation")
    public WTimedAdvancedInnateSkill.Builder createBuilder(AnimationManager.AnimationAccessor<? extends StaticAnimation> animationAccessor,
                                                           String modId) {

        var listenerValues = new ListenerSkillValues(modId, ResourceLocation.fromNamespaceAndPath(modId, name));
        return WTimedAdvancedInnateSkill
                .createTimedBuilder()
                .setAnimations(animationAccessor)
                .setTooltipArray(tooltip)
                .setDisableTooltipProperties(disableTooltipProperties)
                .setActivationSound(soundEvent.map(BuiltInRegistries.SOUND_EVENT::get))
                .setListenerSkillValues(listenerValues)
                .setControlableEvents(controlableEvents)
                .setCategory(SkillCategories.WEAPON_INNATE);
    }

    @Override
    public void applyProperties(WTimedAdvancedInnateSkill builder) {
        properties.forEach(property -> property.applyTo(builder));
    }
}
