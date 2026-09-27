package sleys.efedp.neoforge.task.coroutine;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.neoforge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.sl.library.execution.task.CoroutineTask;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;

public class ServerLaserLineCoroutine extends CoroutineTask {
    private final Level level;
    private final LivingEntity livingCaster;
    private final StunType stunType;
    private final List<Vec3> points;
    private final float damage;
    private final int delay;
    private int index = 0;
    private final int speedModifier;

    public ServerLaserLineCoroutine(Level level, LivingEntity livingCaster, StunType stunType,
                                    List<Vec3> points, float damage, int delay, int speedModifier) {
        this.level = level;
        this.livingCaster = livingCaster;
        this.stunType = stunType;
        this.points = points;
        this.damage = damage;
        this.delay = delay;
        this.speedModifier = speedModifier;
    }

    @Override
    protected boolean run() {
        if (delay == 0) {
            LaserEventHelper.hurtAlongVerticalLazers(
                    level, livingCaster, points,
                    stunType, 30,
                    0.4F, damage
            );
            return false;
        }

        for (int i = 0; i < speedModifier && index < points.size(); i++) {
            this.damagePoint(points.get(index++));
        }

        boolean hasMore = index < points.size();

        if (hasMore) {
            this.waitTicks(delay);
        }

        return hasMore;
    }

    private void damagePoint(Vec3 base) {
        AABB pillar = new AABB(
                base.x - 0.4F, base.y, base.z - 0.4F,
                base.x + 0.4F, base.y + 30, base.z + 0.4F
        );
        level.getEntitiesOfClass(LivingEntity.class, pillar, e -> e != this.livingCaster).forEach(entity -> {
            entity.hurt(EpicFightDamageSources.witherBeam(this.livingCaster).setStunType(stunType), damage);
            LaserEventHelper.impactBurstServer(level, entity.position());
        });
    }
}