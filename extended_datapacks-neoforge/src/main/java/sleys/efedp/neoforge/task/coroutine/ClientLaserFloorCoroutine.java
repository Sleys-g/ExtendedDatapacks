package sleys.efedp.neoforge.task.coroutine;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.neoforge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.sl.library.execution.task.CoroutineTask;

import java.util.List;

public class ClientLaserFloorCoroutine extends CoroutineTask {
    private final Level level;
    private final List<List<Vec3>> pillars;
    private final int delay;
    private final int pointsPerTick;
    private int index = 0;

    public ClientLaserFloorCoroutine(Level level, List<List<Vec3>> pillars, int delay, int pointsPerTick) {
        this.level = level;
        this.pillars = pillars;
        this.delay = delay;
        this.pointsPerTick = pointsPerTick;
    }

    @Override
    protected boolean run() {
        if (delay == 0) {
            pillars.forEach(points -> LaserEventHelper.renderVerticalLazers(level, points, 30));
            return false;
        }

        for (int i = 0; i < pointsPerTick && index < pillars.size(); i++) {
            LaserEventHelper.renderVerticalLazers(level, pillars.get(index++), 30);
        }

        boolean hasMore = index < pillars.size();
        if (hasMore) this.waitTicks(delay);
        return hasMore;
    }
}
