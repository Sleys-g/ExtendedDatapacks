package sleys.efedp;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sleys.efedp.client.commands.ExtendedDatapacksClientCommands;
import sleys.efedp.bootstrap.Bootstrap;
import sleys.efedp.client.config.EpicFightEDPClientConfig;
import sleys.efedp.system.skills.ExtendedDatapacksRegistryCategoryIcon;
import sleys.efedp.system.combat.MechanicsAssignerEvent;
import sleys.efedp.system.combat.ExtendedSkillCategory;
import sleys.efedp.system.combat.ExtendedSkillSlot;
import sleys.efedp.registry.EpicFightConditionsAdderInjector;
import sleys.efedp.registry.ExtendedDatapacksRegistrySkills;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sleys.efedp.system.combat.charged_attacks.ChargedAttackStyles;
import sleys.efedp.client.keybinding.EDPCombatKeyBinding;
import sleys.efedp.config.EpicFightEDPConfig;
import net.minecraftforge.fml.common.Mod;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.LogicalPolicy;
import sleys.sl.library.execution.policy.LogicalTasks;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.item.Style;

@Mod(ExtendedDatapacks.MODID)
public class ExtendedDatapacks {
    public static final Logger LOGGER = LogManager.getLogger(ExtendedDatapacks.class);
	public static final String MODID = "epicfight_edp";
    private static final String CONFIG_PATH = "epicfight_edp/extended_datapack_config.toml";
    private static final String CLIENT_CONFIG_PATH = "epicfight_edp/extended_datapack_client_config.toml";

	public ExtendedDatapacks(FMLJavaModLoadingContext context) {
        final var modBus = context.getModEventBus();
        Bootstrap.start(modBus);

        Style.ENUM_MANAGER.registerEnumCls(MODID, ChargedAttackStyles.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MODID, ExtendedSkillCategory.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(MODID, ExtendedSkillSlot.class);
        MinecraftForge.EVENT_BUS.register(MechanicsAssignerEvent.class);
        modBus.register(ExtendedDatapacksRegistrySkills.class);
        modBus.register(EpicFightConditionsAdderInjector.class);
        context.registerConfig(ModConfig.Type.COMMON, EpicFightEDPConfig.EPICFIGHT_CONFIG, CONFIG_PATH);

        LogicalTasks.run(
                LogicalPolicy.LOGICAL_CLIENT,
                ErrorPolicy.DEPURATE,
                "Extended Datapacks - Client",
                () -> ExtendedDatapacksClient(modBus, context)
        );
	}

    @ErrorHandled
    private static void ExtendedDatapacksClient(IEventBus modBus, FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, EpicFightEDPClientConfig.EDP_CLIENT, CLIENT_CONFIG_PATH);
        modBus.register(EDPCombatKeyBinding.class);
        MinecraftForge.EVENT_BUS.register(ExtendedDatapacksClientCommands.class);
    }
}
