package sleys.efedp.system.animations.json.properties.functional.helpers;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Vector3f;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;

import java.util.ArrayList;
import java.util.List;

public final class LaserEventHelper {

    private LaserEventHelper() {}

    public static Vec3 resolveAimPoint(LivingEntity caster, LivingEntity target, double range, Vec3 targetOffset) {
        if (target != null) return target.position().add(targetOffset);
        return caster.position().add(caster.getLookAngle().add(new Vec3(0, 1, 0)).scale(range));
    }

    public static BlockHitResult clipToBlocks(Level level, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(
                from, to, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, CollisionContext.empty()
        ));
    }

    private static List<Vec3> computeHorizontalPath(Level level, Vec3 from, Vec3 to, double spacing) {
        var clippedTo = clipToBlocks(level, from, to).getLocation();
        double totalDist = from.distanceTo(clippedTo);
        int steps = Math.max(1, (int) Math.floor(totalDist / spacing));
        Vec3 dir = clippedTo.subtract(from).normalize();

        List<Vec3> points = new ArrayList<>(steps + 1);
        for (int i = 0; i <= steps; i++) {
            points.add(from.add(dir.scale(i * spacing)));
        }
        return points;
    }

    private static Vec3 snapToGround(Level level, Vec3 point, double searchUp, double searchDown) {
        Vec3 from = point.add(0, searchUp, 0);
        Vec3 to = point.add(0, -searchDown, 0);
        var hit = clipToBlocks(level, from, to);
        return hit.getType() == HitResult.Type.BLOCK ? hit.getLocation() : point;
    }

    public static List<Vec3> computeGroundedLazerPath(Level level, Vec3 from, Vec3 to,
                                                      double spacing, double searchUp, double searchDown) {
        List<Vec3> horizontal = computeHorizontalPath(level, from, to, spacing);
        List<Vec3> grounded = new ArrayList<>(horizontal.size());
        for (Vec3 p : horizontal) {
            grounded.add(snapToGround(level, p, searchUp, searchDown));
        }
        return grounded;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static OBBCollider buildBeamCollider(Vec3 from, Vec3 to, float halfWidth) {
        var xLength = to.x - from.x;
        var yLength = to.y - from.y;
        var zLength = to.z - from.z;
        var horizontalDistance = Math.sqrt(xLength * xLength + zLength * zLength);
        var length = Math.sqrt(xLength * xLength + yLength * yLength + zLength * zLength);
        var yRot = (float) (-Math.atan2(zLength, xLength) * (180D / Math.PI)) - 90.0F;
        var xRot = (float) (Math.atan2(yLength, horizontalDistance) * (180D / Math.PI));

        var collider = new OBBCollider(halfWidth, halfWidth, length * 0.5D, 0.0F, 0.0F, length * 0.5D);
        collider.transform(
                OpenMatrix4f.createTranslation((float) -from.x, (float) from.y, (float) -from.z)
                        .rotateDeg(yRot, Vec3f.Y_AXIS)
                        .rotateDeg(-xRot, Vec3f.X_AXIS)
        );
        return collider;
    }

    public static void renderBeam(Level level, Vec3 from, Vec3 to) {
        level.addAlwaysVisibleParticle(EpicFightParticles.LASER.get(),
                from.x, from.y + 1, from.z, to.x, to.y + 1, to.z);
    }

    public static void renderVerticalLazers(Level level, List<Vec3> groundedPoints, double laserHeight) {
        for (Vec3 base : groundedPoints) {
            impactZoneClient(level, base);
            level.addAlwaysVisibleParticle(EpicFightParticles.LASER.get(),
                    base.x, base.y, base.z, base.x, base.y + laserHeight, base.z);
        }
    }

    public static void hurtAlongVerticalLazers(Level level, LivingEntity caster, List<Vec3> groundedPoints,
                                               double laserHeight, float halfWidth, float damage) {
        for (Vec3 base : groundedPoints) {
            AABB pillar = new AABB(
                    base.x - halfWidth, base.y, base.z - halfWidth,
                    base.x + halfWidth, base.y + laserHeight, base.z + halfWidth
            );
            level.getEntitiesOfClass(LivingEntity.class, pillar, e -> e != caster).forEach(entity -> {
                entity.hurt(EpicFightDamageSources.witherBeam(caster), damage);
                impactBurstServer(level, entity.position());
            });
        }
    }

    public static void impactZoneClient(Level level, Vec3 pos) {
        level.addAlwaysVisibleParticle(
                new DustParticleOptions(new Vector3f(0.85F, 0.95F, 1.0F), 1.6F),
                pos.x, pos.y, pos.z, 0, 0, 0
        );
        int rays = 6;
        for (int i = 0; i < rays; i++) {
            double angle = i * (Math.PI * 2 / rays);
            double ox = Math.cos(angle) * 0.3;
            double oz = Math.sin(angle) * 0.3;
            level.addAlwaysVisibleParticle(ParticleTypes.END_ROD, pos.x + ox, pos.y + 0.1, pos.z + oz, 0, 0.05, 0);
        }
    }

    public static void impactBurstServer(Level level, Vec3 pos) {
        var serverLevel = (ServerLevel) level;
        serverLevel.sendParticles(
                new DustParticleOptions(new Vector3f(0.85F, 0.95F, 1.0F), 2.2F),
                pos.x, pos.y, pos.z, 3, 0.12, 0.12, 0.12, 0.0
        );

        int rays = 12;
        for (int i = 0; i < rays; i++) {
            double angle = i * (Math.PI * 2 / rays);
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.x, pos.y + 0.1, pos.z, 1, dx * 0.08, 0.12, dz * 0.08, 0.35);
        }

        for (int i = 0; i < 8; i++) {
            double dx = (serverLevel.random.nextDouble() - 0.5) * 2.0;
            double dy = (serverLevel.random.nextDouble() - 0.2) * 1.5;
            double dz = (serverLevel.random.nextDouble() - 0.5) * 2.0;
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= length; dy /= length; dz /= length;
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.x, pos.y + 0.1, pos.z, 1, dx * 0.05, dy * 0.05, dz * 0.05, 0.25);
        }
    }

    public static Vec3 resolveLocalOffset(LivingEntity entity, Vec3 offset) {
        return offset.yRot(-entity.getYRot() * Mth.DEG_TO_RAD);
    }
}