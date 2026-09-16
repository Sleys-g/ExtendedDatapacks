package sleys.efedp.neoforge.system.animations;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import sleys.efedp.neoforge.system.animations.json.groups.config.ConfigAnimationsErrorPool;
import sleys.efedp.neoforge.system.animations.json.groups.config.IAnimationConfig;
import sleys.efedp.neoforge.system.animations.json.builder.AnimationsConfigBuilder;
import sleys.efedp.neoforge.system.animations.json.builder.AnimationsRegistryBuilder;
import sleys.efedp.neoforge.system.animations.json.builder.AnimationsVirtualizationBuilder;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationConfigDefinition;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationAccessorDefinition;
import sleys.efedp.neoforge.system.animations.json.animations.accessor.IAnimationAccessor;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationVirtualizationDefinition;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.groups.virtual.IAnimationVirtualization;
import sleys.efedp.neoforge.system.animations.json.groups.virtual.VirtualConfigAnimationErrorPool;
import sleys.sl.library.exceptions.RegistryObjectModificationException;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.registry.EpicFightRegistries;

import java.util.Objects;
import java.util.stream.Stream;

public class AnimationRegistryOperations {

    @SubscribeEvent
    public static void onRegistryAnimations(AnimationManager.AnimationRegistryEvent event) {
        AnimationsRegistryBuilder
                .getAnimationDefinitionsData()
                .forEach((modId, definitionList) ->
                event.newBuilder(modId, animationBuilder ->
                        definitionList.forEach(def -> registerDef(animationBuilder, def))
                )
        );
    }

    @SubscribeEvent
    public static void onVirtualizeAnimations(RegisterEvent event) {
        if (!event.getRegistry().equals(EpicFightRegistries.SKILL)) return;
        AnimationsVirtualizationBuilder
                .getAnimationVirtualizationData()
                .forEach((modId, virtualizationList) ->
                virtualizationList.forEach(AnimationRegistryOperations::virtualizationDef)
       );
    }

    @SubscribeEvent
    public static void onModifierAnimations(FMLLoadCompleteEvent event) {
        /// Config
        AnimationsConfigBuilder
                .getAnimationConfigData()
                .forEach((modId, configList) ->
                configList.forEach(AnimationRegistryOperations::configDef)
        );

        /// Config Virtual
        AnimationsVirtualizationBuilder
                .getAnimationVirtualizationData()
                .forEach((modId, virtualizationList) ->
                virtualizationList.forEach(AnimationRegistryOperations::configVirtualizationDef)
        );

        onModifierAnimationsError();
    }

    private static void onModifierAnimationsError() {
        var errors = Stream.of(ConfigAnimationsErrorPool.getConfigError(), VirtualConfigAnimationErrorPool.getVirtualConfigError())
                .filter(Objects::nonNull)
                .toList();

        if (!errors.isEmpty()) {
            throw new RegistryObjectModificationException("\n\n" + String.join("\n\n", errors));
        }
    }

    private static <T extends DynamicAnimation> void registerDef(AnimationManager.AnimationBuilder builder,
                                                                 AnimationAccessorDefinition<T> def) {
        IAnimationAccessor<T> accessor = def.accessor();
        IAnimationProperties<T> properties = def.properties();
        accessor.register(builder, properties);
    }

    private static <T extends StaticAnimation> void virtualizationDef(AnimationVirtualizationDefinition<T> vir) {
        IAnimationVirtualization<T> virtual = vir.virtual();
        virtual.setProtocol();
    }

    private static <T extends StaticAnimation> void configVirtualizationDef(AnimationVirtualizationDefinition<T> vir) {
        IAnimationVirtualization<T> virtual = vir.virtual();
        IAnimationProperties<T> properties = vir.properties();
        virtual.configProtocol(properties);
    }

    private static <T extends StaticAnimation> void configDef(AnimationConfigDefinition<T> cfg) {
        IAnimationConfig<T> config = cfg.config();
        IAnimationProperties<T> properties = cfg.properties();
        config.applyConfig(properties);
    }
}
