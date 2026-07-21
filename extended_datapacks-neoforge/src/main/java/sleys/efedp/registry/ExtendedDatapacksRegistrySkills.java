package sleys.efedp.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.combat.charged_attacks.ChargedAttack;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

public class ExtendedDatapacksRegistrySkills {
    public static final DeferredRegister<Skill> REGISTRY  = DeferredRegister.create(EpicFightRegistries.Keys.SKILL, ExtendedDatapacks.MODID);

    public static final DeferredHolder<Skill, ChargedAttack> CHARGED_ATTACK = REGISTRY.register(
            "charged_attack",
            key -> ChargedAttack.createChargedAttackBuilder()
                    .build(key)
    );
}
