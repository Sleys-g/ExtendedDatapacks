package sleys.efedp.system.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.client.config.EpicFightEDPClientConfig;
import sleys.efedp.system.weapons.json.WeaponsPassiveParticle;
import sleys.sl.epicfight.capability.StyleInvalid;
import sleys.sl.epicfight.model.weaponry.ShapeParticleEngine;
import sleys.sl.library.client.particle.emitters.MutableSimpleParticleEmitter;
import sleys.sl.library.execution.task.CoroutineTask;
import sleys.sl.library.network.particle.IParticleEmitterPacket;
import sleys.sl.library.util.helper.player.PlayerHelper;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.List;

public class WeaponsPassiveParticlesCoroutineRunner extends CoroutineTask {
    private final MutableSimpleParticleEmitter emitter = new MutableSimpleParticleEmitter(null);
    private final Minecraft mc = Minecraft.getInstance();

    public WeaponsPassiveParticlesCoroutineRunner() {
        ExtendedDatapacks.LOGGER.info(
                "[Weapons Passive Particle - Coroutine Runner] Cleaning & Rebooting Weapons Passive Particle Coroutine!"
        );
    }

    @Override
    protected boolean run() {
        if (!EpicFightEDPClientConfig.getSeePassiveParticles()) return true;

        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (player == null || level == null) return true;
        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof Player viwerPlayer)) continue;

            if (viwerPlayer.distanceToSqr(player) > EpicFightEDPClientConfig.getParticleViewer()) continue;
            if (!PlayerHelper.isValidPlayer(viwerPlayer)) continue;

            if (viwerPlayer.getMainHandItem().isEmpty() && viwerPlayer.getOffhandItem().isEmpty()) continue;
            this.inViewerRendering(player);
        }
        return true;
    }

    @SuppressWarnings("all")
    private void inViewerRendering(Player player) {
        var patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        for (InteractionHand hand : InteractionHand.values()) {
            var handStack = player.getItemInHand(hand);
            if (handStack.isEmpty()) continue;

            var itemCapability = EpicFightCapabilities.getItemCapability(handStack);
            if (itemCapability.isEmpty()) continue;

            var style = itemCapability.get().getStyle(patch);
            if (style == null || style.equals(StyleInvalid.INVALID)) continue;

            ResourceLocation rlItem = BuiltInRegistries.ITEM.getKey(handStack.getItem());
            List<WeaponsPassiveParticle.PassiveParticleEntry> emitters = WeaponsPassiveParticle.getEntries(rlItem, style);
            for (WeaponsPassiveParticle.PassiveParticleEntry emitter : emitters) {
                SimpleParticleType particle = emitter.getPassiveParticle();
                if (particle == null) continue;

                Vec3 speed = emitter.passiveSpeed();
                IParticleEmitterPacket packet = speed != null ?
                        this.emitter.setParticle(particle, speed) :
                        this.emitter.setParticle(particle);

                ShapeParticleEngine.generateHandParticles(
                        player, hand, packet, emitter.passiveAmount()
                );
            }
        }
    }
}
