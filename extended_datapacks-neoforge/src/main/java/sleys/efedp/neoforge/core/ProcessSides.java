package sleys.efedp.neoforge.core;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public enum ProcessSides {
    CLIENT(ProcessSides::onClient, ProcessSides::onClientLevel),
    SERVER(ProcessSides::onServer, ProcessSides::onServerLevel),
    BOTH(ProcessSides::onBoth, ProcessSides::onBothLevel),
    LOCAL_CLIENT(ProcessSides::onLocalClient, ProcessSides::onLocalClientLevel);

    private final Predicate<Entity> predicateEntity;
    private final Predicate<Level> predicateLevel;

    ProcessSides(Predicate<Entity> predicateEntity, Predicate<Level> predicateLevel) {
        this.predicateEntity = predicateEntity;
        this.predicateLevel = predicateLevel;
    }

    private static boolean onClient(Entity entity) {
        return onClientLevel(entity.level());
    }

    private static boolean onServer(Entity entity) {
        return onServerLevel(entity.level());
    }

    private static boolean onLocalClient(Entity entity) {
        return onLocalClientLevel(entity.level());
    }

    private static boolean onClientLevel(Level level) {
        return level.isClientSide;
    }

    private static boolean onBoth(Entity entity) {
        return true;
    }

    private static boolean onBothLevel(Level level) {
        return true;
    }

    private static boolean onServerLevel(Level level) {
        return !level.isClientSide;
    }

    private static boolean onLocalClientLevel(Level level) {
        return !FMLEnvironment.dist.isDedicatedServer() && level.isClientSide;
    }

    public boolean validateSide(Entity entity) {
        return predicateEntity.test(entity);
    }

    public boolean validateSide(Level level) {
        return predicateLevel.test(level);
    }

    public boolean validateSideOr(Entity entity, Runnable callback) {
        var result = this.validateSide(entity);
        if (!result) callback.run();
        return result;
    }

    public boolean validateSideOr(Level level, Runnable callback) {
        var result = this.validateSide(level);
        if (!result) callback.run();
        return result;
    }

    public boolean validateSideOrLog(Entity entity, String message, BiConsumer<ProcessSides, String> loggerAs) {
        var result = this.validateSide(entity);
        if (!result) loggerAs.accept(this, message);
        return result;
    }

    public boolean validateSideOrLog(Level level, String message, BiConsumer<ProcessSides, String> loggerAs) {
        var result = this.validateSide(level);
        if (!result) loggerAs.accept(this, message);
        return result;
    }

    public AnimationEvent.Side toEpicFightSides() {
        return switch (this) {
            case CLIENT -> AnimationEvent.Side.CLIENT;
            case SERVER -> AnimationEvent.Side.SERVER;
            case BOTH -> AnimationEvent.Side.BOTH;
            case LOCAL_CLIENT -> AnimationEvent.Side.LOCAL_CLIENT;
        };
    }

    public static AnimationEvent.Side toEpicFightSides(ProcessSides sides) {
        return switch (sides) {
            case CLIENT -> AnimationEvent.Side.CLIENT;
            case SERVER -> AnimationEvent.Side.SERVER;
            case BOTH -> AnimationEvent.Side.BOTH;
            case LOCAL_CLIENT -> AnimationEvent.Side.LOCAL_CLIENT;
        };
    }

    public static final Codec<ProcessSides> PROCESS_SIDES_CODEC = EnumCodecs.byId(
            values(), e -> e.name().toUpperCase(Locale.ROOT)
    );

    public static final Codec<AnimationEvent.Side> EPICFIGHT_SIDES_CODEC = EnumCodecs.byId(
            AnimationEvent.Side.values(), e -> e.name().toUpperCase(Locale.ROOT)
    );
}
