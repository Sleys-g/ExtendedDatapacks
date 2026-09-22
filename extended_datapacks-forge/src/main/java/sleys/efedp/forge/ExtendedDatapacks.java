package sleys.efedp.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sleys.efedp.forge.capability.data.HitParticleCache;
import sleys.efedp.forge.client.commands.ExtendedDatapacksClientCommands;
import sleys.efedp.forge.bootstrap.Bootstrap;
import sleys.efedp.forge.client.config.EpicFightEDPClientConfig;
import sleys.efedp.forge.events.ControlDamageEvent;
import sleys.efedp.forge.registry.*;
import sleys.efedp.forge.system.combat.MechanicsAssignerEvent;
import sleys.efedp.forge.system.combat.ExtendedSkillCategory;
import sleys.efedp.forge.system.combat.ExtendedSkillSlot;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sleys.efedp.forge.system.combat.charged_attacks.ChargedAttackStyles;
import sleys.efedp.forge.client.keybinding.EDPCombatKeyBinding;
import sleys.efedp.forge.config.EpicFightEDPConfig;
import net.minecraftforge.fml.common.Mod;
import sleys.sl.library.SLLPreferences;
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
    public static final String VERSION = "2.4.6";

    private static final String CONFIG_PATH = "epicfight_edp/extended_datapack_config.toml";
    private static final String CLIENT_CONFIG_PATH = "epicfight_edp/extended_datapack_client_config.toml";

	public ExtendedDatapacks(FMLJavaModLoadingContext context) {
        final var modBus = context.getModEventBus();
        Bootstrap.start(modBus);

        Style.ENUM_MANAGER.registerEnumCls(MODID, ChargedAttackStyles.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MODID, ExtendedSkillCategory.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(MODID, ExtendedSkillSlot.class);

        MinecraftForge.EVENT_BUS.register(MechanicsAssignerEvent.class);
        MinecraftForge.EVENT_BUS.register(ControlDamageEvent.class);

        modBus.register(HitParticleCache.class);
        modBus.register(ExtendedDatapacksAttributes.class);
        modBus.register(ExtendedDatapacksSkills.class);
        modBus.register(ExtendedDatapacksConditions.class);
        modBus.addListener(ExtendedDatapacksEntitiesArmatures::registerEntitiesArmatures);
        modBus.register(ExtendedDatapacksPatchesEntities.class);

        ExtendedDatapacksEntities.REGISTRY.register(modBus);
        context.registerConfig(ModConfig.Type.COMMON, EpicFightEDPConfig.EPICFIGHT_CONFIG, CONFIG_PATH);

        LogicalTasks.run(
                LogicalPolicy.LOGICAL_CLIENT, ErrorPolicy.DEPURATE,
                "Extended Datapacks - Client",
                () -> ExtendedDatapacks.ExtendedDatapacksClient(modBus, context)
        );
//
//        SLLPreferences.turnOnGameTest();
	}

    @ErrorHandled
    private static void ExtendedDatapacksClient(IEventBus modBus, FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, EpicFightEDPClientConfig.EDP_CLIENT, CLIENT_CONFIG_PATH);
        MinecraftForge.EVENT_BUS.register(ExtendedDatapacksClientCommands.class);
        modBus.register(EDPCombatKeyBinding.class);
        modBus.register(ExtendedDatapacksRenders.class);
        modBus.register(ExtendedDatapacksPatchesRenders.class);
    }
}
