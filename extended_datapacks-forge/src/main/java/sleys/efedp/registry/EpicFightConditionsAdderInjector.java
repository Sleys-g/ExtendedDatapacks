package sleys.efedp.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.conditions.charged.ChargedAttackCondition;
import sleys.efedp.conditions.entity.*;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.main.EpicFightMod;

import java.util.function.Supplier;

public class EpicFightConditionsAdderInjector {

    @SubscribeEvent
    public static void onRegisterEvent(RegisterEvent event) {
        ResourceKey<? extends Registry<?>> key = event.getRegistryKey();

        if (!key.location().equals(EpicFightMod.identifier("conditions"))) return;

        IForgeRegistry<Supplier<Condition<?>>> registry = event.getForgeRegistry();
        if (registry == null) {
            ExtendedDatapacks.LOGGER.error(
                    "A strange error has occurred where the registration is null; " +
                            "therefore, the Epic Fight EDP conditional registration has been skipped."
            );
        } else {
            EpicFightConditionsAdderInjector.registryConditions(registry);
        }
    }

    public static void registryConditions(IForgeRegistry<Supplier<Condition<?>>> event) {
        event.register(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "passive_skill_boolean_data_key"),
                PassiveSkillDataKeyBooleanCondition::new
        );

        event.register(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "passive_skill_float_data_key"),
                PassiveSkillDataKeyFloatCondition::new
        );

        event.register(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "passive_skill_integer_data_key"),
                PassiveSkillDataKeyIntegerCondition::new
        );

        event.register(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "innate_skill_boolean_data_key"),
                InnateSkillDataKeyBooleanCondition::new
        );

        event.register(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "innate_skill_float_data_key"),
                InnateSkillDataKeyFloatCondition::new
        );

        event.register(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "innate_skill_integer_data_key"),
                InnateSkillDataKeyIntegerCondition::new
        );

        event.register(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "charged_attack_event"),
                ChargedAttackCondition::new
        );
    }
}
