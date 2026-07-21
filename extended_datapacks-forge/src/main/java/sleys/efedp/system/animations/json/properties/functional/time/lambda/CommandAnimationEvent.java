package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record CommandAnimationEvent(String command) implements IAnimationEventParams {

    public static final MapCodec<CommandAnimationEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("command").forGetter(CommandAnimationEvent::command)
            ).apply(instance, CommandAnimationEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER,"Command Event")) {
            return;
        }

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
