package sleys.efedp;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sleys.efedp.capability.data.HitParticleCache;
import sleys.efedp.capability.data.SkillDataKeyCache;
import sleys.efedp.client.commands.ExtendedDatapacksClientCommands;
import sleys.efedp.bootstrap.Bootstrap;
import sleys.efedp.client.config.EpicFightEDPClientConfig;
import sleys.efedp.registry.ExtendedDatapacksConditions;
import sleys.efedp.registry.ExtendedDatapacksRegistrySkills;
import sleys.efedp.system.combat.ExtendedSkillCategory;
import sleys.efedp.system.combat.MechanicsAssignerEvent;
import sleys.efedp.system.combat.ExtendedSkillSlot;
import sleys.efedp.system.combat.charged_attacks.ChargedAttackStyles;
import sleys.efedp.client.keybinding.EDPCombatKeyBinding;
import sleys.efedp.config.EpicFightEDPConfig;
import sleys.sl.library.execution.policy.LogicalTasks;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.LogicalPolicy;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.item.Style;

@Mod(ExtendedDatapacks.MODID)
public class ExtendedDatapacks {
    public static final Logger LOGGER = LogManager.getLogger(ExtendedDatapacks.class);
    public static final String MODID = "epicfight_edp";
    private static final String CONFIG_PATH = "epicfight_edp/extended_datapack_config.toml";
    private static final String CLIENT_CONFIG_PATH = "epicfight_edp/extended_datapack_client_config.toml";

    public ExtendedDatapacks(IEventBus modBus, ModContainer modContainer) {
        Bootstrap.start(modBus);
        Style.ENUM_MANAGER.registerEnumCls(MODID, ChargedAttackStyles.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MODID, ExtendedSkillCategory.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(MODID, ExtendedSkillSlot.class);
        NeoForge.EVENT_BUS.register(MechanicsAssignerEvent.class);
        modBus.register(SkillDataKeyCache.class);
        modBus.register(HitParticleCache.class);
        ExtendedDatapacksRegistrySkills.REGISTRY.register(modBus);
        ExtendedDatapacksConditions.CONDITIONS.register(modBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, EpicFightEDPConfig.EDP, CONFIG_PATH);

        LogicalTasks.run(
                LogicalPolicy.LOGICAL_CLIENT, ErrorPolicy.DEPURATE,
                "Extended Datapacks - Client",
                () -> ExtendedDatapacks.ExtendedDatapacksClient(modBus, modContainer)
        );
    }

    private static void ExtendedDatapacksClient(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, EpicFightEDPClientConfig.EDP_CLIENT, CLIENT_CONFIG_PATH);
        NeoForge.EVENT_BUS.register(ExtendedDatapacksClientCommands.class);
        modBus.register(EDPCombatKeyBinding.class);
    }
}