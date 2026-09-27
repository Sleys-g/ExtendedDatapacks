package sleys.efedp.forge.task.coroutine;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.sl.library.execution.task.CoroutineTask;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;

public class ServerLaserShapeCoroutine extends CoroutineTask {

    private final Level level;
    private final LivingEntity livingCaster;
    private final StunType stunType;
    private final List<Vec3> points;
    private final float damage;
    private final int delay;

    private int index = 0;
    private final int laserHeight;
    private final int speedMultiplier;

    public ServerLaserShapeCoroutine(Level level, LivingEntity livingCaster, StunType stunType,
                                     List<Vec3> points, float damage, int delay,
                                     int laserHeight, int speedMultiplier) {
        this.level = level;
        this.livingCaster = livingCaster;
        this.stunType = stunType;
        this.points = points;
        this.damage = damage;
        this.delay = delay;
        this.laserHeight = laserHeight;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    protected boolean run() {
        if (delay == 0) {
            LaserEventHelper.hurtAlongVerticalLazers(level, livingCaster, points, stunType, laserHeight, 0.4F, damage);
            return false;
        }

        for (int i = 0; i < speedMultiplier && index < points.size(); i++) this.damagePoint(points.get(index++));
        boolean hasMore = index < points.size();
        if (hasMore) this.waitTicks(delay);
        return hasMore;
    }

    private void damagePoint(Vec3 base) {
        AABB pillar = new AABB(
                base.x - 0.4F, base.y, base.z - 0.4F,
                base.x + 0.4F, base.y + laserHeight, base.z + 0.4F
        );

        level.getEntitiesOfClass(LivingEntity.class, pillar, entity -> entity != this.livingCaster)
                .forEach(entity -> {
                    entity.hurt(EpicFightDamageSources.witherBeam(this.livingCaster).setStunType(stunType), damage);
                    LaserEventHelper.impactBurstServer(level, entity.position());
                });
    }
}