package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.library.annotations.OpaqueMethod;
import sleys.sl.library.util.data.codec.EnumCodecs;
import sleys.sl.library.util.helper.entity.EntityHelper;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public record SummonEntityOnTargetEvent(ResourceLocation entityType,
                                        TargetMode targetMode,
                                        float area) implements IAnimationEventParams {
    private static final Map<Class<?>, Method> OWNER_METHOD_CACHE = new ConcurrentHashMap<>();

    public static final MapCodec<SummonEntityOnTargetEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(SummonEntityOnTargetEvent::entityType),
                    TargetMode.CODEC.fieldOf("target_mode").forGetter(SummonEntityOnTargetEvent::targetMode),
                    Codec.FLOAT.fieldOf("area").forGetter(SummonEntityOnTargetEvent::area)
            ).apply(instance, SummonEntityOnTargetEvent::new)
    );

    @Override @SuppressWarnings("deprecation")
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.SERVER, "Summon Entity On Target")) return;
        if (!(caster.level() instanceof ServerLevel serverLevel)) return;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityType);

        switch (targetMode) {
            case TARGET -> {
                var target = patch.getTarget();
                if (target != null) spawnAt(serverLevel, type, target, caster);
            }
            case TARGET_GROUP -> EntityHelper.executeFunctionOnEntitiesAABB(
                    caster, caster.level(), area, EntityHelper.TargetMethod.SELECTIVE,
                    patch.getTarget(), entity -> spawnAt(serverLevel, type, entity, caster)
            );
            case ALL_HOSTILE -> EntityHelper.executeFunctionOnEntitiesAABB(
                    caster, caster.level(), area, EntityHelper.TargetMethod.HOSTILE,
                    patch.getTarget(), entity -> spawnAt(serverLevel, type, entity, caster)
            );
            case ALL -> EntityHelper.executeFunctionOnEntitiesAABB(
                    caster, caster.level(), area, EntityHelper.TargetMethod.ALL,
                    patch.getTarget(), entity -> {
                        if (canHarm(caster, entity, serverLevel)) {
                            spawnAt(serverLevel, type, entity, caster);
                        }
                    }
            );
        }
    }

    private static boolean canHarm(LivingEntity caster, LivingEntity target, ServerLevel level) {
        if (!(target instanceof Player)) return true;

        MinecraftServer server = level.getServer();
        if (caster instanceof Player) {
            return server.isPvpAllowed();
        }

        return true;
    }

    private void spawnAt(ServerLevel level, EntityType<?> type, LivingEntity target, LivingEntity caster) {
        Vec3 pos = target.position();
        if (target instanceof OwnableEntity) return;

        Entity raw = type.create(level);
        if (raw == null) return;
        if (!(raw instanceof OwnableEntity)) {
            raw.discard();
            ExtendedDatapacks.LOGGER.warn(
                    "[Summon Entity On Target] entity_type '{}' it is not an OwnableEntity, not allowed in attack events.",
                    type
            );
            return;
        }

        raw.moveTo(pos.x, pos.y, pos.z, caster.getYRot(), 0);

        if (raw instanceof Mob mob) {
            mob.setTarget(target);
            startOwnerGambit(raw, caster);
        }
        level.addFreshEntity(raw);
    }

    @OpaqueMethod
    private void startOwnerGambit(Entity raw, LivingEntity caster) {
        Method cached = OWNER_METHOD_CACHE.get(raw.getClass());
        if (cached != null) {
            try {
                invokeOwnerSetter(cached, raw, caster);
            } catch (ReflectiveOperationException e) {
                ExtendedDatapacks.LOGGER.error("[Summon Entity On Entity] Couldn't invoke cached owner setter", e);
            }
            return;
        }

        for (Method method : raw.getClass().getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) continue;

            if (method.getName().equals("setOwner")) {
                if (params[0] == LivingEntity.class || params[0] == Entity.class) {
                    OWNER_METHOD_CACHE.put(raw.getClass(), method);
                    try {
                        method.invoke(raw, caster);
                    } catch (ReflectiveOperationException e) {
                        ExtendedDatapacks.LOGGER.error("[Summon Entity On Entity] Couldn't invoke owner setter", e);
                    }
                    return;
                }
            }

            if (method.getName().equals("setOwnerUUID") && params[0] == UUID.class) {
                OWNER_METHOD_CACHE.put(raw.getClass(), method);
                try {
                    method.invoke(raw, caster.getUUID());
                } catch (ReflectiveOperationException e) {
                    ExtendedDatapacks.LOGGER.error("[Summon Entity On Entity] Couldn't invoke owner setter", e);
                }
                return;
            }
        }
    }

    private void invokeOwnerSetter(Method method, Entity raw, LivingEntity caster) throws ReflectiveOperationException {
        Class<?> param = method.getParameterTypes()[0];
        if (param == LivingEntity.class || param == Entity.class) {
            method.invoke(raw, caster);
        } else if (param == UUID.class) {
            method.invoke(raw, caster.getUUID());
        }
    }


    private enum TargetMode {
        TARGET,
        TARGET_GROUP,
        ALL_HOSTILE,
        ALL;

        public static final Codec<TargetMode> CODEC = EnumCodecs.byId(values(),
                e -> e.name().toUpperCase(Locale.ROOT));
    }
}