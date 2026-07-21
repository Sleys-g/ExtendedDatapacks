package sleys.efedp.system.animations.json.virtual;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.phase.AttackPhaseProperties;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;

import java.util.List;

public record VirtualAttackAnimationGroup(ResourceLocation realAnimation, ResourceLocation virtualAnimation,
                                          List<AttackPhaseProperties> properties) implements IVirtualAnimation<AttackAnimation> {

    public static final MapCodec<VirtualAttackAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("real_animation")
                            .forGetter(VirtualAttackAnimationGroup::realAnimation),

                    ResourceLocation.CODEC.fieldOf("virtual_animation")
                            .forGetter(VirtualAttackAnimationGroup::virtualAnimation),

                    AttackPhaseProperties.CODEC.codec().listOf().optionalFieldOf("properties", List.of())
                            .forGetter(VirtualAttackAnimationGroup::properties)
            ).apply(instance, VirtualAttackAnimationGroup::new)
    );


    @Override
    public AnimationGroupType virtualGroupType() {
        return AnimationGroupType.ATTACK_GROUP;
    }

    @Override
    public void configProtocol(IAnimationProperty<AttackAnimation> property) {
        var animation = getAnimation().get();
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
    }
}
