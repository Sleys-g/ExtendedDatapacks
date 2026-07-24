package sleys.efedp.client.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleys.efedp.system.visuals.json.OverlayAssetPacksSystem;
import sleys.efedp.system.visuals.json.ShaderAssetsPacksSystem;
import sleys.efedp.system.weapons.json.WeaponAdvancedSwingTrail;
import sleys.efedp.system.weapons.json.WeaponPerStyleModelBaker;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@OnlyIn(Dist.CLIENT)
public class ExtendedDatapacksClientCommands {

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("sl")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("dependent")
                                .then(Commands.literal("extended-datapacks")
                                        .then(Commands.literal("reload")
                                                .then(Commands.literal("models-assets-pack")
                                                        .executes(ExtendedDatapacksClientCommands::reloadItemModelsConfigClient)
                                                )
                                                .then(Commands.literal("advanced-swing-trails")
                                                        .executes(ExtendedDatapacksClientCommands::reloadSwingTrailConfigClient)
                                                )
                                                .then(Commands.literal("shader-effect-packs")
                                                        .executes(ExtendedDatapacksClientCommands::reloadShaderPacksConfigClient)
                                                )
                                                .then(Commands.literal("overlay-effect-packs")
                                                        .executes(ExtendedDatapacksClientCommands::reloadOverlayConfigClient)
                                                )
                                        )
                                        .then(Commands.literal("get")
                                                .then(Commands.literal("weapon-category")
                                                        .executes(ExtendedDatapacksClientCommands::getActuallyCategory)
                                                )
                                                .then(Commands.literal("weapon-passive")
                                                        .executes(ExtendedDatapacksClientCommands::getActuallyPassive)
                                                )
                                                .then(Commands.literal("weapon-skill")
                                                        .executes(ExtendedDatapacksClientCommands::getActuallyInnate)
                                                )
                                        )
                                )
                        )
        );
    }

    @SuppressWarnings("removal")
    private static int getActuallyPassive(CommandContext<CommandSourceStack> context) {
        Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        var itemCapability = EpicFightCapabilities.getItemCapability(player.getMainHandItem());
        if (itemCapability.isEmpty()) return 0;
        player.displayClientMessage(Component.literal("Your handed weapon passive is: " + itemCapability.get().getPassiveSkill()), false);
        return 1;
    }

    private static int getActuallyInnate(CommandContext<CommandSourceStack> context) {
        Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        var item = player.getMainHandItem();
        var itemCapability = EpicFightCapabilities.getItemCapability(item);
        var patchPlayer = EpicFightCapabilities.getPlayerPatch(player);
        if (itemCapability.isEmpty() || patchPlayer == null) return 0;
        player.displayClientMessage(Component.literal("Your handed weapon innate is: " + itemCapability.get().getInnateSkill(patchPlayer, item)), false);
        return 1;
    }

    private static int getActuallyCategory(CommandContext<CommandSourceStack> context) {
        Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        var itemCapability = EpicFightCapabilities.getItemCapability(player.getMainHandItem());
        if (itemCapability.isEmpty()) return 0;
        player.displayClientMessage(Component.literal("Your handed weapon category is: " + itemCapability.get().getWeaponCategory()), false);
        return 1;
    }

    private static int reloadOverlayConfigClient(CommandContext<CommandSourceStack> context) {
        Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        player.displayClientMessage(Component.literal("Reassigning Overlay configuration!"), false);
        OverlayAssetPacksSystem.reinitializeOverlayAssetPack();
        player.displayClientMessage(Component.literal("Done!"), false);
        return 1;
    }

    private static int reloadItemModelsConfigClient(CommandContext<CommandSourceStack> context) {
        Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        player.displayClientMessage(Component.literal("Reassigning Item Model configuration!"), false);
        WeaponPerStyleModelBaker.reinitialize();
        player.displayClientMessage(Component.literal("Done!"), false);
        return 1;
    }

    private static int reloadSwingTrailConfigClient(CommandContext<CommandSourceStack> context) {
        Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        player.displayClientMessage(Component.literal("Reassigning Swing Trails configuration!"), false);
        WeaponAdvancedSwingTrail.reinitializeAdvancedSwingTrail();
        player.displayClientMessage(Component.literal("Done!"), false);
        return 1;
    }

    private static int reloadShaderPacksConfigClient(CommandContext<CommandSourceStack> context) {
        Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return 0;
        player.displayClientMessage(Component.literal("Reassigning Shader Packs configuration!"), false);
        ShaderAssetsPacksSystem.reinitializeShaderAssetsPack();
        player.displayClientMessage(Component.literal("Done!"), false);
        return 1;
    }

}


