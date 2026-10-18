package sleys.efedp.forge.system.animations.groups.types;

import sleys.efedp.forge.system.animations.groups.config.ConfigActionAnimationGroup;
import sleys.efedp.forge.system.animations.groups.config.ConfigAttackAnimationGroup;
import sleys.efedp.forge.system.animations.groups.config.ConfigStaticAnimationGroup;
import sleys.efedp.forge.system.animations.definitions.AnimationConfigDefinitionCodec;
import sleys.efedp.forge.system.animations.definitions.AnimationVirtualizationDefinitionCodec;
import sleys.efedp.forge.system.animations.groups.registry.IAnimationGroupType;
import sleys.efedp.forge.system.animations.groups.virtual.VirtualActionAnimationGroup;
import sleys.efedp.forge.system.animations.groups.virtual.VirtualAttackAnimationGroup;
import sleys.efedp.forge.system.animations.groups.virtual.VirtualStaticAnimationGroup;
import sleys.efedp.forge.system.animations.properties.ActionAnimationProperties;
import sleys.efedp.forge.system.animations.properties.AttackAnimationProperties;
import sleys.efedp.forge.system.animations.properties.StaticAnimationProperties;

public enum EpicFightAnimationGroups implements IAnimationGroupType {
    STATIC_GROUP("epicfight_edp:static_group",
            new AnimationVirtualizationDefinitionCodec<>(VirtualStaticAnimationGroup.CODEC, StaticAnimationProperties.codec()),
            new AnimationConfigDefinitionCodec<>(ConfigStaticAnimationGroup.CODEC, StaticAnimationProperties.codec())
    ),
    ATTACK_GROUP("epicfight_edp:attack_group",
            new AnimationVirtualizationDefinitionCodec<>(VirtualActionAnimationGroup.CODEC, ActionAnimationProperties.codec()),
            new AnimationConfigDefinitionCodec<>(ConfigActionAnimationGroup.CODEC, ActionAnimationProperties.codec())
    ),
    ACTION_GROUP("epicfight_edp:action_group",
            new AnimationVirtualizationDefinitionCodec<>(VirtualAttackAnimationGroup.CODEC, AttackAnimationProperties.codec()),
            new AnimationConfigDefinitionCodec<>(ConfigAttackAnimationGroup.CODEC, AttackAnimationProperties.codec())
    );

    private final String id;
    private final AnimationVirtualizationDefinitionCodec<?> virtualDefinitionCodec;
    private final AnimationConfigDefinitionCodec<?> configDefinitionCodec;

    EpicFightAnimationGroups(String id, AnimationVirtualizationDefinitionCodec<?> virtualDefinitionCodec,
                             AnimationConfigDefinitionCodec<?> configDefinitionCodec) {
        this.id = id;
        this.virtualDefinitionCodec = virtualDefinitionCodec;
        this.configDefinitionCodec = configDefinitionCodec;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public AnimationVirtualizationDefinitionCodec<?> virtualDefinitionCodec() {
        return virtualDefinitionCodec;
    }

    @Override
    public AnimationConfigDefinitionCodec<?> configDefinitionCodec() {
        return configDefinitionCodec;
    }
}
