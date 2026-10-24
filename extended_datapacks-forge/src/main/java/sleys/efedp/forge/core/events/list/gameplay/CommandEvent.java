package sleys.efedp.forge.core.events.list.gameplay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.forge.core.events.IEventParams;
import sleys.efedp.forge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record CommandEvent(String command) implements IEventParams {

    public static final MapCodec<CommandEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("command").forGetter(CommandEvent::command)
            ).apply(instance, CommandEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), ProcessSides.SERVER,"Command Event")) return;
        this.executeEvent(livingCaster);
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingCaster) {
        if (this.isInvalid(livingCaster.level(), ProcessSides.SERVER,"Command Event")) return;
        this.executeEvent(livingCaster);
    }

    private void executeEvent(LivingEntity livingCaster) {
        MinecraftServer server = livingCaster.getServer();
        if (server != null) {
            server.getCommands().performPrefixedCommand(
                    livingCaster.createCommandSourceStack()
                            .withPermission(4)
                            .withSuppressedOutput(),
                    command
            );
        }
    }
}
