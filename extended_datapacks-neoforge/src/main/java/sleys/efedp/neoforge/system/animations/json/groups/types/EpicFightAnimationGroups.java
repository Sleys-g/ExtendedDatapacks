package sleys.efedp.neoforge.system.animations.json.groups.types;

import sleys.efedp.neoforge.system.animations.json.groups.config.ConfigActionAnimationGroup;
import sleys.efedp.neoforge.system.animations.json.groups.config.ConfigAttackAnimationGroup;
import sleys.efedp.neoforge.system.animations.json.groups.config.ConfigStaticAnimationGroup;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationConfigDefinitionCodec;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationVirtualizationDefinitionCodec;
import sleys.efedp.neoforge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.neoforge.system.animations.json.properties.ActionAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.AttackAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.StaticAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.groups.virtual.VirtualActionAnimationGroup;
import sleys.efedp.neoforge.system.animations.json.groups.virtual.VirtualAttackAnimationGroup;
import sleys.efedp.neoforge.system.animations.json.groups.virtual.VirtualStaticAnimationGroup;

public enum EpicFightAnimationGroups implements IAnimationGroupType {
    STATIC_GROUP("epicfight_edp:static_group",
            new AnimationVirtualizationDefinitionCodec<>(VirtualStaticAnimationGroup.CODEC, StaticAnimationProperties.CODEC),
            new AnimationConfigDefinitionCodec<>(ConfigStaticAnimationGroup.CODEC, StaticAnimationProperties.CODEC)
    ),
    ATTACK_GROUP("epicfight_edp:attack_group",
            new AnimationVirtualizationDefinitionCodec<>(VirtualActionAnimationGroup.CODEC, ActionAnimationProperties.CODEC),
            new AnimationConfigDefinitionCodec<>(ConfigActionAnimationGroup.CODEC, ActionAnimationProperties.CODEC)
    ),
    ACTION_GROUP("epicfight_edp:action_group",
            new AnimationVirtualizationDefinitionCodec<>(VirtualAttackAnimationGroup.CODEC, AttackAnimationProperties.CODEC),
            new AnimationConfigDefinitionCodec<>(ConfigAttackAnimationGroup.CODEC, AttackAnimationProperties.CODEC)
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
