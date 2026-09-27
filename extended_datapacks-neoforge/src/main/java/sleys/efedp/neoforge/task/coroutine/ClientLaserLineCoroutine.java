package sleys.efedp.neoforge.task.coroutine;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.neoforge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.sl.library.execution.task.CoroutineTask;
import yesman.epicfight.registry.entries.EpicFightParticles;

import java.util.List;

public class ClientLaserLineCoroutine extends CoroutineTask {
    private final Level level;
    private final List<Vec3> points;
    private final int delay;
    private int index = 0;
    private final int speedModifier;

    public ClientLaserLineCoroutine(Level level, List<Vec3> points, int delay, int speedModifier) {
        this.level = level;
        this.points = points;
        this.delay = delay;
        this.speedModifier = speedModifier;
    }

    @Override
    protected boolean run() {
        if (delay == 0) {
            LaserEventHelper.renderVerticalLazers(level, points, 30);
            return false;
        }

        for (int i = 0; i < speedModifier && index < points.size(); i++) {
            this.renderPoint(points.get(index++));
        }

        boolean hasMore = index < points.size();

        if (hasMore) {
            this.waitTicks(delay);
        }

        return hasMore;
    }

    private void renderPoint(Vec3 base) {
        LaserEventHelper.impactZoneClient(level, base);
        level.addAlwaysVisibleParticle(
                EpicFightParticles.LASER.get(),
                base.x, base.y, base.z, base.x, base.y + 30, base.z
        );
    }
}
