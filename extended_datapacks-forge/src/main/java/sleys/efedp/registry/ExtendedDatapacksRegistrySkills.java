package sleys.efedp.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.efedp.system.combat.charged_attacks.ChargedAttack;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;

public class ExtendedDatapacksRegistrySkills {
    public static Skill CHARGED_ATTACK;

    @SubscribeEvent
    public static void buildSkillEvent(SkillBuildEvent build) {
        SkillBuildEvent.ModRegistryWorker modRegistry = build.createRegistryWorker(ExtendedDatapacks.MODID);

        CHARGED_ATTACK = modRegistry.build("charged_attack",
                ChargedAttack::new, ChargedAttack.createChargedAttackBuilder().setResource(Skill.Resource.STAMINA)
        );
    }
}
