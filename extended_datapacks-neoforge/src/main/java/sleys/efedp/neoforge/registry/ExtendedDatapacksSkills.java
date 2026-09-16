package sleys.efedp.neoforge.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.combat.charged_attacks.ChargedAttack;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

public class ExtendedDatapacksSkills {
    public static final DeferredRegister<Skill> REGISTRY  = DeferredRegister.create(EpicFightRegistries.Keys.SKILL, ExtendedDatapacks.MODID);

    public static final DeferredHolder<Skill, ChargedAttack> CHARGED_ATTACK = REGISTRY.register(
            "charged_attack",
            key -> ChargedAttack.createChargedAttackBuilder()
                    .build(key)
    );
}
