package sleys.efedp.neoforge.task.coroutine;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.neoforge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.sl.library.execution.task.CoroutineTask;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;

public class ServerLaserFloorCoroutine extends CoroutineTask {
    private final Level level;
    private final LivingEntity livingCaster;
    private final StunType stunType;
    private final List<List<Vec3>> pillars;
    private final float damage;
    private final int delay;
    private final int pointsPerTick;
    private int index = 0;

    public ServerLaserFloorCoroutine(Level level, LivingEntity livingCaster, StunType stunType,
                                     List<List<Vec3>> pillars, float damage, int delay, int pointsPerTick) {
        this.level = level;
        this.livingCaster = livingCaster;
        this.stunType = stunType;
        this.pillars = pillars;
        this.damage = damage;
        this.delay = delay;
        this.pointsPerTick = pointsPerTick;
    }

    @Override
    protected boolean run() {
        if (delay == 0) {
            pillars.forEach(points -> LaserEventHelper.hurtAlongVerticalLazers(
                    level, livingCaster, points, stunType, 30, 0.4F, damage));
            return false;
        }

        for (int i = 0; i < pointsPerTick && index < pillars.size(); i++) {
            LaserEventHelper.hurtAlongVerticalLazers(
                    level, livingCaster, pillars.get(index++), stunType, 30, 0.4F, damage);
        }

        boolean hasMore = index < pillars.size();
        if (hasMore) this.waitTicks(delay);
        return hasMore;
    }
}