package sleys.efedp.neoforge.plataform.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.combat.charged_attacks.ChargedAttack;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.Skill;

public class ExtendedDatapacksSkills {

    public static final DeferredRegister<Skill> REGISTRY  = ExtendedDatapacks.IDENTIFIER.registry(EpicFightRegistries.Keys.SKILL);

    public static final DeferredHolder<Skill, ChargedAttack> CHARGED_ATTACK = REGISTRY.register(
            "charged_attack",
            key -> ChargedAttack.createChargedAttackBuilder()
                    .build(key)
    );
}
