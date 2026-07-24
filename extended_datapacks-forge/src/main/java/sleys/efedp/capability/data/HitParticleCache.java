package sleys.efedp.capability.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;

import java.util.HashMap;
import java.util.Map;

public class HitParticleCache {

    private static final Map<ResourceLocation, RegistryObject<HitParticleType>> CACHE = new HashMap<>();
    private static boolean INITIALIZED = false;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(HitParticleCache::registryCache);
    }

    @SuppressWarnings("unchecked")
    private static synchronized void registryCache() {
        if (!INITIALIZED) {
            for (var registryObj : EpicFightParticles.PARTICLES.getEntries()) {
                var type = registryObj.getKey();
                if (type == null) continue;

                ResourceLocation id = registryObj.getKey().location();
                if (!(registryObj.get() instanceof HitParticleType)) continue;
                RegistryObject<HitParticleType> typed = (RegistryObject<HitParticleType>) (RegistryObject<?>) registryObj;
                CACHE.put(id, typed);
            }

            INITIALIZED = true;
        }
    }

    public static RegistryObject<HitParticleType> getParticleDeferred(ResourceLocation rl) {
        return CACHE.get(rl);
    }

    public static void addParticleDeferred(ResourceLocation id, RegistryObject<HitParticleType> hitParticle) {
        CACHE.put(id, hitParticle);
    }
}
