package sleys.efedp.neoforge.plataform.registry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.core.events.list.gameplay.tick.ClientLaserVerticalImpactTask;
import sleys.sl.library.execution.task.TickTaskType;
import sleys.sl.library.network.task.STCTickTask;

import java.util.function.Consumer;

public final class ExtendedDatapacksTickTaskList {

    public static final ResourceLocation LASER_VERTICAL_IMPACT_TASK =
            ExtendedDatapacks.IDENTIFIER.location("laser_vertical_impact");

    public static final TickTaskType<ClientLaserVerticalImpactTask> LASER_VERTICAL_IMPACT =
            new TickTaskType<>(
                    ExtendedDatapacksTickTaskList.LASER_VERTICAL_IMPACT_TASK,
                    ClientLaserVerticalImpactTask::new
            );

    public static void sendToPlayersTrackingEntityAndSelf(ResourceLocation taskId, LivingEntity caster, Consumer<FriendlyByteBuf> buff) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(caster, new STCTickTask(taskId.toString(), buff, null));
    }
}
