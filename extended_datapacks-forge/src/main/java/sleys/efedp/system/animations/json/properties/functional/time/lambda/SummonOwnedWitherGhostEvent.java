package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import sleys.efedp.world.entities.OwnableWitherGhost;
import sleys.sl.library.util.helper.entity.EntityHelper;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Optional;

public record SummonOwnedWitherGhostEvent(Optional<Float> damage,
                                          Optional<Float> area,
                                          Optional<Boolean> allowAreaDamage,
                                          Optional<EntityHelper.TargetMethod> targetMode) implements IAnimationEventParams {

    public static final MapCodec<SummonOwnedWitherGhostEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("damage").forGetter(SummonOwnedWitherGhostEvent::damage),
                    Codec.FLOAT.optionalFieldOf("area").forGetter(SummonOwnedWitherGhostEvent::area),
                    Codec.BOOL.optionalFieldOf("area_damage").forGetter(SummonOwnedWitherGhostEvent::allowAreaDamage),
                    EntityHelper.TargetMethod.CODEC.optionalFieldOf("target_mode").forGetter(SummonOwnedWitherGhostEvent::targetMode)
            ).apply(instance, SummonOwnedWitherGhostEvent::new)
    );
    
    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER,"Summon Owned Wither Ghost Event")) {
            return;
        }

        var level = livingCaster.level();
        var livingTarget = patch.getTarget();
        if (area.isPresent()) this.onAreaEffect(area.get(), level, livingCaster, livingTarget);
        else this.onSummonEntity(level, livingCaster, livingTarget);
    }

    private void onAreaEffect(Float areaSize, Level level, LivingEntity livingCaster, LivingEntity target) {
        var targetAreaMode = targetMode.orElse(EntityHelper.TargetMethod.BOTH);
        EntityHelper.executeFunctionOnCasterAndEntitiesAABB(
                livingCaster, level, areaSize, targetAreaMode, target,
                candidate -> this.onSummonEntity(level, livingCaster, candidate)
        );
    }

    private void onSummonEntity(Level level, LivingEntity livingCaster, LivingEntity target) {
        if (target == null) return;
        var witherGhost = new OwnableWitherGhost((ServerLevel) level, target.position(), target);
        witherGhost.setOwnerUUID(livingCaster.getUUID());
        damage.ifPresent(witherGhost::setDamage);
        allowAreaDamage.ifPresent(witherGhost::setAreaDamage);
        level.addFreshEntity(witherGhost);
    }
}
