package sleys.efedp.registry;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.conditions.charged.ChargedAttackCondition;
import sleys.efedp.conditions.entity.*;
import yesman.epicfight.EpicFight;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.registry.EpicFightRegistries;

import java.util.function.Supplier;

public class ExtendedDatapacksConditions {
    public static final DeferredRegister<Supplier<Condition<?>>> CONDITIONS  = DeferredRegister.create(EpicFightRegistries.CONDITION, EpicFight.MODID);

    public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> PASSIVE_SKILL_DATA_KEY_BOOLEAN  = CONDITIONS.register(
            ResourceLocation.fromNamespaceAndPath(EpicFight.MODID, "passive_skill_boolean_data_key")
                    .getPath(), () -> PassiveSkillDataKeyBooleanCondition::new
    );

    public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> PASSIVE_SKILL_DATA_KEY_FLOAT = CONDITIONS.register(
            ResourceLocation.fromNamespaceAndPath(EpicFight.MODID, "passive_skill_float_data_key")
                    .getPath(), () -> PassiveSkillDataKeyFloatCondition::new
    );

    public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> PASSIVE_SKILL_DATA_KEY_INTEGER = CONDITIONS.register(
            ResourceLocation.fromNamespaceAndPath(EpicFight.MODID, "passive_skill_integer_data_key")
                    .getPath(), () -> PassiveSkillDataKeyIntegerCondition::new
    );

    public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> INNATE_SKILL_DATA_KEY_BOOLEAN = CONDITIONS.register(
            ResourceLocation.fromNamespaceAndPath(EpicFight.MODID, "innate_skill_boolean_data_key")
                    .getPath(), () -> InnateSkillDataKeyBooleanCondition::new
    );

    public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> INNATE_SKILL_DATA_KEY_FLOAT = CONDITIONS.register(
            ResourceLocation.fromNamespaceAndPath(EpicFight.MODID, "innate_skill_float_data_key")
                    .getPath(), () -> InnateSkillDataKeyFloatCondition::new
    );

    public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> INNATE_SKILL_DATA_KEY_INTEGER = CONDITIONS.register(
            ResourceLocation.fromNamespaceAndPath(EpicFight.MODID, "innate_skill_integer_data_key")
                    .getPath(), () -> InnateSkillDataKeyIntegerCondition::new
    );

    public static final DeferredHolder<Supplier<Condition<?>>, Supplier<Condition<?>>> CHARGED_ATTACK_EVENT = CONDITIONS.register(
            ResourceLocation.fromNamespaceAndPath(EpicFight.MODID, "charged_attack_event")
                    .getPath(), () -> ChargedAttackCondition::new
    );
}
