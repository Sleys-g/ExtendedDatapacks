package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.main.ExtendedDatapacks;
import sleys.sl.epicfight.model.JointModelCordReader;
import sleys.sl.library.util.data.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Locale;

public record JointShootProjectileEvent(
        ResourceLocation entityType,
        String joint,
        float poseTime,
        Vec3 offset,

        ProjectileDirection direction,
        double speed,
        float inaccuracy,
        float damage,
        boolean gravity,
        boolean piercing
) implements IAnimationEventParams {

    public static final MapCodec<JointShootProjectileEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(JointShootProjectileEvent::entityType),
                    Codec.STRING.fieldOf("joint").forGetter(JointShootProjectileEvent::joint),
                    Codec.FLOAT.fieldOf("pose_time").forGetter(JointShootProjectileEvent::poseTime),
                    Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(JointShootProjectileEvent::offset),
                    ProjectileDirection.CODEC.fieldOf("direction").forGetter(JointShootProjectileEvent::direction),
                    Codec.DOUBLE.fieldOf("speed").forGetter(JointShootProjectileEvent::speed),
                    Codec.FLOAT.fieldOf("inaccuracy").forGetter(JointShootProjectileEvent::inaccuracy),
                    Codec.FLOAT.fieldOf("damage").forGetter(JointShootProjectileEvent::damage),
                    Codec.BOOL.optionalFieldOf("gravity", false).forGetter(JointShootProjectileEvent::gravity),
                    Codec.BOOL.optionalFieldOf("piercing", false).forGetter(JointShootProjectileEvent::piercing)
            ).apply(instance, JointShootProjectileEvent::new)
    );


    @Override @SuppressWarnings("deprecation")
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.SERVER, "Joint Shoot Projectile")) return;
        if (!(caster.level() instanceof ServerLevel serverLevel)) return;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityType);
        Entity raw = type.create(serverLevel);

        if (!(raw instanceof Projectile projectile)) {
            if (raw != null) raw.discard();
            ExtendedDatapacks.LOGGER.warn(
                    "[Joint Shoot Projectile] entity_type '{}' it's not a Projectile, the event is ignored.",
                    entityType
            );
            return;
        }

        Vec3 localHandCord = JointModelCordReader.getModelWorldPosition(
                patch, patch.getArmature().searchJointByName(joint),
                poseTime, Vec3.ZERO
        );
        Vec3 origin = localHandCord.add(offset);

        projectile.setPos(origin.x, origin.y, origin.z);
        projectile.setOwner(caster);

        Vec3 dir = resolveDirection(caster, patch, origin);
        projectile.shoot(dir.x, dir.y, dir.z, (float) speed, inaccuracy);

        if (!gravity) {
            projectile.setNoGravity(true);
        }

        if (projectile instanceof AbstractArrow arrow) {
            if (damage >= 0) arrow.setBaseDamage(damage);
            if (piercing) arrow.setPierceLevel((byte) 5);
        }

        serverLevel.addFreshEntity(projectile);
    }

    private Vec3 resolveDirection(LivingEntity caster, LivingEntityPatch<?> patch, Vec3 spawnCords) {
        return switch (direction) {
            case LOOK -> caster.getLookAngle();
            case TARGET -> {
                var target = patch.getTarget();
                yield target != null
                        ? target.position().subtract(caster.position()).normalize()
                        : caster.getLookAngle();
            }
            case HOMING -> {
                var target = patch.getTarget();
                yield target != null
                        ? resolveHoming(target, spawnCords)
                        : caster.getLookAngle();
            }
            case FORWARD_FLAT -> caster.getLookAngle().multiply(1, 0, 1).normalize();
        };
    }

    private Vec3 resolveHoming(LivingEntity target, Vec3 spawnCords) {
        Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
        Vec3 targetMotion = target.getDeltaMovement();
        Vec3 predictedPos = targetPos.add(targetMotion.scale(0.5));
        return predictedPos.subtract(spawnCords).normalize();
    }

    private enum ProjectileDirection {
        LOOK, TARGET, HOMING, FORWARD_FLAT;
        public static final Codec<ProjectileDirection> CODEC = EnumCodecs.byId(values(),
                e -> e.name().toUpperCase(Locale.ROOT));
    }
}