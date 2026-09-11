package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;

import java.util.List;
import java.util.Optional;

public record ThunderAnimationEvent(Optional<Boolean> changeAmbient) implements IAnimationEventParams {

    public static final MapCodec<ThunderAnimationEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("change_ambient").forGetter(ThunderAnimationEvent::changeAmbient)
            ).apply(instance, ThunderAnimationEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER,"Thunder Event")) return;

        T animation = accessor.get();
        if (!(animation instanceof AttackAnimation attackAnimation)) return;

        AttackAnimation.Phase phase = attackAnimation.phases[0];
        int maxStrikes = (int) ValueModifier.calculator()
                .attach(phase.getProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER).orElse(ValueModifier.setter(3.0F)))
                .getResult(0.0F);

        float damage = ValueModifier.calculator()
                .attach(phase.getProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER).orElse(ValueModifier.setter(8.0F)))
                .getResult(0.0F);

        LivingEntity attacker = patch.getOriginal();
        ServerLevel level = (ServerLevel) attacker.level();

        float totalDamage = damage
                + ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])
                .get(attacker, attacker.getItemInHand(InteractionHand.MAIN_HAND), null, damage);

        List<Entity> targets = HitEntityList.Priority.HOSTILITY.sort(
                patch,
                level.getEntities(
                        attacker,
                        attacker.getBoundingBox().inflate(10.0D, 4.0D, 10.0D),
                        entity -> entity.distanceToSqr(attacker) <= 100.0D
                                && !entity.isAlliedTo(attacker)
                                && attacker.hasLineOfSight(entity)
                )
        );

        ServerPlayer cause = patch instanceof ServerPlayerPatch playerPatch
                ? playerPatch.getOriginal()
                : null;

        int hits = Math.min(maxStrikes, targets.size());
        this.createLightningBolt(hits, targets, level, cause, attacker, attackAnimation, patch, phase, totalDamage);
        if (hits > 0) {
            changeAmbient
                    .map(key -> key ? level : null)
                    .ifPresent(this::changeAmbient);
            attacker.playSound(
                    SoundEvents.TRIDENT_THUNDER.value(),
                    5.0F,
                    1.0F
            );
        }
    }

    private void createLightningBolt(int hits, List<Entity> targets, ServerLevel level, ServerPlayer cause,
                                     LivingEntity attacker, AttackAnimation attackAnimation, LivingEntityPatch<?> patch,
                                     AttackAnimation.Phase phase, float totalDamage) {
        for (int i = 0; i < hits; i++) {
            Entity target = targets.get(i);

            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
            if (lightning == null) {
                continue;
            }

            lightning.setVisualOnly(true);
            lightning.moveTo(Vec3.atBottomCenterOf(target.blockPosition()));
            lightning.setDamage(0.0F);
            lightning.setCause(cause);

            DamageSource vanillaSource = new DamageSource(
                    target.level()
                            .registryAccess()
                            .registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(DamageTypes.LIGHTNING_BOLT),
                    attacker
            );

            EpicFightDamageSource epicSource = attackAnimation
                    .getEpicFightDamageSource(vanillaSource, patch, target, phase)
                    .setUsedItem(attacker.getItemInHand(InteractionHand.MAIN_HAND));

            target.hurt(epicSource, totalDamage);
            target.thunderHit(level, lightning);
            level.addFreshEntity(lightning);
        }
    }

    private void changeAmbient(ServerLevel level) {
        if (level == null) return;
        if (level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)
                && level.random.nextFloat() < 0.08F
                && level.getThunderLevel(1.0F) < 1.0F) {

            level.setWeatherParameters(
                    0,
                    Mth.randomBetweenInclusive(level.random, 12000, 180000),
                    true, true
            );
        }
    }
}
