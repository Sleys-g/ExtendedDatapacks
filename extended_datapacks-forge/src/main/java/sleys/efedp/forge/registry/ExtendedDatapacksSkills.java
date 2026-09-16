package sleys.efedp.forge.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.combat.charged_attacks.ChargedAttack;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;

public class ExtendedDatapacksSkills {
    public static Skill CHARGED_ATTACK;

    @SubscribeEvent
    public static void buildSkillEvent(SkillBuildEvent build) {
        SkillBuildEvent.ModRegistryWorker modRegistry = build.createRegistryWorker(ExtendedDatapacks.MODID);

        CHARGED_ATTACK = modRegistry.build("charged_attack",
                ChargedAttack::new, ChargedAttack.createChargedAttackBuilder().setResource(Skill.Resource.STAMINA)
        );
    }
}
