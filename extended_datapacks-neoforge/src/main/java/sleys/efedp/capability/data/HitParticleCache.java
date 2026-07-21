package sleys.efedp.capability.data;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.registry.entries.EpicFightParticles;

import java.util.HashMap;
import java.util.Map;

public class HitParticleCache {

    private static final Map<ResourceLocation, DeferredHolder<ParticleType<?>, HitParticleType>> CACHE = new HashMap<>();
    private static boolean INITIALIZED = false;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(HitParticleCache::registryCache);
    }

    @SuppressWarnings("unchecked")
    private static synchronized void registryCache() {
        if (INITIALIZED) return;
        for (var holder : EpicFightParticles.REGISTRY.getEntries()) {
            ResourceLocation id = holder.getKey().location();
            if (!(holder.get() instanceof HitParticleType)) continue;
            DeferredHolder<ParticleType<?>, HitParticleType> typed = (DeferredHolder<ParticleType<?>, HitParticleType>) holder;
            CACHE.put(id, typed);
        }

        INITIALIZED = true;
    }

    public static DeferredHolder<ParticleType<?>, HitParticleType> getParticleDeferred(ResourceLocation rl) {
        return CACHE.get(rl);
    }

    public static void addParticleDeferred(ResourceLocation id, DeferredHolder<ParticleType<?>, HitParticleType> hitParticle) {
        CACHE.put(id, hitParticle);
    }
}
