package sleys.efedp.forge.task.tick;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.sl.library.execution.task.ITickTask;
import sleys.sl.library.execution.task.TickTaskType;
import yesman.epicfight.gameasset.EpicFightSounds;

public class ClientLaserVerticalImpactTask implements ITickTask {
    public static final TickTaskType<ClientLaserVerticalImpactTask> TYPE =
            new TickTaskType<>(
                    TickTaskList.LASER_VERTICAL_IMPACT_TASK,
                    ClientLaserVerticalImpactTask::new
            );

    private final int casterId;
    private final int targetId;

    public ClientLaserVerticalImpactTask(FriendlyByteBuf buf) {
        this.casterId = buf.readInt();
        this.targetId = buf.readInt();
    }

    @Override
    public boolean execute() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;


        Entity caster = level.getEntity(casterId);
        Entity target = level.getEntity(targetId);

        if (!(caster instanceof LivingEntity livingCaster)) return false;
        if (!(target instanceof LivingEntity livingTarget)) return false;

        render(livingCaster, livingTarget);
        return false;
    }

    private void render(LivingEntity caster, LivingEntity target) {
        var level = caster.level();
        caster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 1F);
        var targetPos = LaserEventHelper.resolveAimPoint(caster, target, 1, Vec3.ZERO);
        var points = LaserEventHelper.computeGroundedLazerPath(
                level, targetPos, targetPos,
                1.3, 3.0, 32.0
        );
        LaserEventHelper.renderVerticalLazers(level, points, 30);
    }
}