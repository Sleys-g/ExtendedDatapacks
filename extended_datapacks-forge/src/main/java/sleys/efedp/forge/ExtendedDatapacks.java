package sleys.efedp.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sleys.efedp.forge.epicfight.data.HitParticleCache;
import sleys.efedp.forge.client.commands.ExtendedDatapacksClientCommands;
import sleys.efedp.forge.bootstrap.Bootstrap;
import sleys.efedp.forge.client.config.ExtendedDatapacksClientConfig;
import sleys.efedp.forge.plataform.events.ControlDamageEvent;
import sleys.efedp.forge.plataform.registry.*;
import sleys.efedp.forge.system.combat.MechanicsAssignerEvent;
import sleys.efedp.forge.system.combat.ExtendedSkillCategory;
import sleys.efedp.forge.system.combat.ExtendedSkillSlot;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sleys.efedp.forge.system.combat.charged_attacks.ChargedAttackStyles;
import sleys.efedp.forge.client.keybinding.ExtendedDatapacksCombatKeyBinding;
import sleys.efedp.forge.plataform.config.EpicFightEDPConfig;
import net.minecraftforge.fml.common.Mod;
import sleys.efedp.forge.core.events.list.gameplay.tick.ClientLaserVerticalImpactTask;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ErrorPolicy;
import sleys.sl.library.execution.policy.LogicalPolicy;
import sleys.sl.library.execution.policy.LogicalTasks;
import sleys.sl.library.execution.task.TickTaskRegistry;
import sleys.sl.library.util.data.mod.ModIdentifier;
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

    public static final ModIdentifier IDENTIFIER = new ModIdentifier(MODID);

	public ExtendedDatapacks(FMLJavaModLoadingContext context) {
        final var modBus = context.getModEventBus();
        Bootstrap.start(modBus);

        Style.ENUM_MANAGER.registerEnumCls(MODID, ChargedAttackStyles.class);
        SkillCategory.ENUM_MANAGER.registerEnumCls(MODID, ExtendedSkillCategory.class);
        SkillSlot.ENUM_MANAGER.registerEnumCls(MODID, ExtendedSkillSlot.class);

        MinecraftForge.EVENT_BUS.register(MechanicsAssignerEvent.class);
        MinecraftForge.EVENT_BUS.register(ControlDamageEvent.class);

        modBus.addListener(ExtendedDatapacks::registerClientPayload);
        modBus.register(HitParticleCache.class);
        modBus.register(ExtendedDatapacksAttributes.class);
        modBus.register(ExtendedDatapacksSkills.class);
        modBus.register(ExtendedDatapacksConditions.class);
        modBus.addListener(ExtendedDatapacksEntitiesArmatures::registerEntitiesArmatures);
        modBus.register(ExtendedDatapacksPatchesEntities.class);

        ExtendedDatapacksEntities.REGISTRY.register(modBus);
        context.registerConfig(ModConfig.Type.COMMON, EpicFightEDPConfig.EPICFIGHT_CONFIG, CONFIG_PATH);

        if (LogicalPolicy.LOGICAL_CLIENT.isValidSide()) ExtendedDatapacks.ExtendedDatapacksClient(modBus, context);
//        SLLPreferences.turnOnGameTest();
	}

    @ErrorHandled
    private static void ExtendedDatapacksClient(IEventBus modBus, FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, ExtendedDatapacksClientConfig.EDP_CLIENT, CLIENT_CONFIG_PATH);
        MinecraftForge.EVENT_BUS.register(ExtendedDatapacksClientCommands.class);
        modBus.register(ExtendedDatapacksCombatKeyBinding.class);
        modBus.register(ExtendedDatapacksRenders.class);
        modBus.register(ExtendedDatapacksPatchesRenders.class);
    }

    public static void registerClientPayload(FMLClientSetupEvent event) {
        event.enqueueWork(ExtendedDatapacks::registryTickTask);
    }

    private static void registryTickTask() {
        TickTaskRegistry.register(ExtendedDatapacksTickTaskList.LASER_VERTICAL_IMPACT);
    }
}
