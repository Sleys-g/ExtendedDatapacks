package sleys.efedp.system.animations;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import sleys.efedp.system.animations.json.config.IConfigAnimation;
import sleys.efedp.system.animations.json.definitions.AnimationsConfigBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationsRegistryBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationsVirtualBuilder;
import sleys.efedp.system.animations.json.definitions.config.AnimationConfigDefinition;
import sleys.efedp.system.animations.json.definitions.registry.AnimationRegistryDefinition;
import sleys.efedp.system.animations.json.accessor.IAnimationAccessor;
import sleys.efedp.system.animations.json.definitions.virtualization.AnimationVirtualDefinition;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.efedp.system.animations.json.virtual.IVirtualAnimation;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.registry.EpicFightRegistries;

public class AnimationRegistryOperations {

    @SubscribeEvent
    public static void onRegistryAnimations(AnimationManager.AnimationRegistryEvent event) {
        AnimationsRegistryBuilder.getAnimationDefinitionsData().forEach((modId, definitionList) ->
                event.newBuilder(modId, animationBuilder -> {
                    for (AnimationRegistryDefinition<?> def : definitionList) {
                        registerDef(animationBuilder, def);
                    }
                })
        );
    }

    @SubscribeEvent
    public static void onVirtualizateAnimations(RegisterEvent event) {
        if (event.getRegistry().equals(EpicFightRegistries.SKILL)) {
            AnimationsVirtualBuilder.getAnimationVirtualizationData().forEach((modId, virtualizationList) -> {
                for (AnimationVirtualDefinition<?> vir : virtualizationList) {
                    virtualizationDef(vir);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onModifierAnimations(FMLLoadCompleteEvent event) {
        AnimationsConfigBuilder.getAnimationConfigData().forEach((modId, configList) -> {
            for (AnimationConfigDefinition<?> cfg : configList) {
                configDef(cfg);
            }
        });

        AnimationsVirtualBuilder.getAnimationVirtualizationData().forEach((modId, virtualizationList) -> {
            for (AnimationVirtualDefinition<?> vir : virtualizationList) {
                configVirtualizationDef(vir);
            }
        });
    }

    private static <T extends DynamicAnimation> void registerDef(AnimationManager.AnimationBuilder builder,
                                                                 AnimationRegistryDefinition<T> def) {
        IAnimationAccessor<T> accessor = def.accessor();
        IAnimationProperty<T> properties = def.properties();
        accessor.register(builder, properties);
    }

    private static <T extends StaticAnimation> void virtualizationDef(AnimationVirtualDefinition<T> vir) {
        IVirtualAnimation<T> virtual = vir.virtual();
        virtual.setProtocol();
    }

    private static <T extends StaticAnimation> void configVirtualizationDef(AnimationVirtualDefinition<T> vir) {
        IVirtualAnimation<T> virtual = vir.virtual();
        IAnimationProperty<T> properties = vir.properties();
        virtual.configProtocol(properties);
    }

    private static <T extends StaticAnimation> void configDef(AnimationConfigDefinition<T> cfg) {
        IConfigAnimation<T> config = cfg.config();
        IAnimationProperty<T> properties = cfg.properties();
        config.applyConfig(properties);
    }
}
