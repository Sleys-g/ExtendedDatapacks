package sleys.efedp.capability.data;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.registry.entries.EpicFightSkillDataKeys;
import yesman.epicfight.skill.SkillDataKey;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SkillDataKeyCache {

    private static final Map<ResourceLocation, DeferredHolder<SkillDataKey<?>, ? extends SkillDataKey<?>>> CACHE = new HashMap<>();
    private static boolean INITIALIZED = false;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(SkillDataKeyCache::registryCache);
    }

    private static synchronized void registryCache() {
        if (INITIALIZED) return;
        CACHE.clear();

        for (var holder : EpicFightSkillDataKeys.REGISTRY.getEntries()) {
            ResourceLocation id = holder.getKey().location();

            if (CACHE.containsKey(id)) throw new IllegalStateException("Duplicate SkillDataKey ID detected: " + id);
            CACHE.put(id, holder);
        }

        INITIALIZED = true;
    }

    @Nullable
    private static DeferredHolder<SkillDataKey<?>, ? extends SkillDataKey<?>> getSkillDataKey(ResourceLocation id) {
        if (INITIALIZED) return CACHE.get(id);
        throw new IllegalStateException("Skill Data Key Cache accessed before initialization");
    }

    @SuppressWarnings("unchecked") @Nullable
    public static <O> DeferredHolder<SkillDataKey<?>, SkillDataKey<O>> getSkillDataKey(ResourceLocation id, Class<O> targetType) {
        var deferredHolder = SkillDataKeyCache.getSkillDataKey(id);
        if (deferredHolder == null || targetType == null) return null;
        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                () -> (DeferredHolder<SkillDataKey<?>, SkillDataKey<O>>) deferredHolder,
                null
        );
    }
}
