package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.sl.library.annotations.OpaqueMethod;
import sleys.sl.library.util.helper.entity.EntityHelper;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public record SummonOwnedEntityOnTargetEvent(ResourceLocation entityType,
                                             Optional<Float> area,
                                             Optional<EntityHelper.TargetMethod> targetMode) implements IAnimationEventParams {

    private static final Map<Class<?>, Method> OWNER_METHOD_CACHE = new ConcurrentHashMap<>();

    public static final MapCodec<SummonOwnedEntityOnTargetEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(SummonOwnedEntityOnTargetEvent::entityType),
                    Codec.FLOAT.optionalFieldOf("area").forGetter(SummonOwnedEntityOnTargetEvent::area),
                    EntityHelper.TargetMethod.CODEC.optionalFieldOf("target_mode").forGetter(SummonOwnedEntityOnTargetEvent::targetMode)
            ).apply(instance, SummonOwnedEntityOnTargetEvent::new)
    );

    @Override @SuppressWarnings("deprecation")
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER, "Summon Owned Entity On Target Event")) return;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityType);
        var level = livingCaster.level();
        var livingTarget = patch.getTarget();

        if (area.isPresent()) this.onAreaEffect(area.get(), type, level, livingCaster, livingTarget);
        else this.onSummonEntity(level, type, livingCaster, livingTarget);
    }

    private void onAreaEffect(Float areaSize, EntityType<?> entityType, Level level, LivingEntity livingCaster, LivingEntity target) {
        var targetAreaMode = targetMode.orElse(EntityHelper.TargetMethod.BOTH);
        EntityHelper.executeFunctionOnCasterAndEntitiesAABB(
                livingCaster, level, areaSize, targetAreaMode, target,
                candidate -> this.onSummonEntity(level, entityType, livingCaster, candidate)
        );
    }

    private void onSummonEntity(Level level, EntityType<?> entityType, LivingEntity livingCaster, LivingEntity target) {
        if (target == null) return;
        Vec3 pos = target.position();
        if (target instanceof OwnableEntity) return;

        Entity raw = entityType.create(level);
        if (raw == null) return;
        if (!(raw instanceof OwnableEntity)) {
            raw.discard();
            ExtendedDatapacks.LOGGER.warn(
                    "[Summon Owned Entity On Target] entity_type '{}' it is not an OwnableEntity, not allowed in attack events.",
                    entityType
            );
            return;
        }

        raw.moveTo(pos.x, pos.y, pos.z, livingCaster.getYRot(), 0);

        if (raw instanceof Mob mob) {
            mob.setTarget(target);
            startOwnerGambit(raw, livingCaster);
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
                ExtendedDatapacks.LOGGER.error("[Summon Owned Entity On Target] Couldn't invoke cached owner setter", e);
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
                        ExtendedDatapacks.LOGGER.error("[Summon Owned Entity On Target] Couldn't invoke owner setter", e);
                    }
                    return;
                }
            }

            if (method.getName().equals("setOwnerUUID") && params[0] == UUID.class) {
                OWNER_METHOD_CACHE.put(raw.getClass(), method);
                try {
                    method.invoke(raw, caster.getUUID());
                } catch (ReflectiveOperationException e) {
                    ExtendedDatapacks.LOGGER.error("[Summon Owned Entity On Target] Couldn't invoke owner setter", e);
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
}