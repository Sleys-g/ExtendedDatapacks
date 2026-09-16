package sleys.efedp.neoforge.system.animations.json.groups.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.neoforge.system.animations.json.groups.types.EpicFightAnimationGroups;
import sleys.efedp.neoforge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.neoforge.system.animations.json.properties.phase.AttackPhaseProperties;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.types.AttackAnimation;

import java.util.List;

public record ConfigAttackAnimationGroup(ResourceLocation animation, List<AttackPhaseProperties> properties
) implements IAnimationConfig<AttackAnimation> {

    public static final MapCodec<ConfigAttackAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("animation")
                            .forGetter(ConfigAttackAnimationGroup::animation),
                    AttackPhaseProperties.CODEC.codec().listOf().optionalFieldOf("properties", List.of())
                            .forGetter(ConfigAttackAnimationGroup::properties)
            ).apply(instance, ConfigAttackAnimationGroup::new)
    );


    @Override
    public IAnimationGroupType groupType() {
        return EpicFightAnimationGroups.ATTACK_GROUP;
    }

    @Override
    public void applyConfig(IAnimationProperties<AttackAnimation> property) {
        if (this.isInvalidAccessor()) return;
        var animation = getAccessor().get();
        var phases = animation.phases;
        int phasesSize = phases.length;
        int propertiesSize = properties.size();

        if (!properties.isEmpty()) {
            for (int i = 0; i < phasesSize; i++) {
                int propertyIndex = Math.min(i, propertiesSize - 1);

                var phase = phases[i];
                var phaseProperty = properties.get(propertyIndex);
                phaseProperty.applyTo(phase);
            }
        }

        property.applyTo(animation);
        this.isSuccessful();
    }
}
