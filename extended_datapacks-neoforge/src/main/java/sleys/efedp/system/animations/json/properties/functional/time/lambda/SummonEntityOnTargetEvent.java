package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sleys.sl.library.util.helper.entity.EntityHelper;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Optional;

public record SummonEntityOnTargetEvent(ResourceLocation entityType,
                                        Optional<Float> area,
                                        Optional<EntityHelper.TargetMethod> targetMode) implements IAnimationEventParams {

    public static final MapCodec<SummonEntityOnTargetEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(SummonEntityOnTargetEvent::entityType),
                    Codec.FLOAT.optionalFieldOf("area").forGetter(SummonEntityOnTargetEvent::area),
                    EntityHelper.TargetMethod.CODEC.optionalFieldOf("target_mode").forGetter(SummonEntityOnTargetEvent::targetMode)
            ).apply(instance, SummonEntityOnTargetEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER, "Summon Entity On Target Event")) {
            return;
        }

        var type = BuiltInRegistries.ENTITY_TYPE.get(entityType);
        var level = livingCaster.level();
        var livingTarget = patch.getTarget();

        if (area.isPresent()) this.onAreaEffect(area.get(), type, level, livingCaster, livingTarget);
        else this.onSummonEntity(level, type, livingCaster, livingTarget);
    }

    private void onAreaEffect(Float areaSize, EntityType<?> entityType, Level level, LivingEntity livingCaster, LivingEntity target) {
        var targetAreaMode = targetMode.orElse(EntityHelper.TargetMethod.BOTH);
        EntityHelper.executeFunctionOnCasterAndEntitiesAABB(
                livingCaster, level, areaSize, targetAreaMode, target,
                candidate -> this.onSummonEntity(level, entityType, livingCaster, candidate)
        );
    }

    private void onSummonEntity(Level level, EntityType<?> entityType, LivingEntity livingCaster, LivingEntity target) {
        if (target == null) return;

        Vec3 pos = target.position();
        Entity raw = entityType.create(level);
        if (raw == null) return;

        raw.moveTo(pos.x, pos.y, pos.z, livingCaster.getYRot(), 0);
        if (raw instanceof Mob mob) mob.setTarget(target);

        level.addFreshEntity(raw);
    }
}