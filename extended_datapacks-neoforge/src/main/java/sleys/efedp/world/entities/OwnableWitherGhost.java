package sleys.efedp.world.entities;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.registry.ExtendedDatapacksEntities;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import yesman.epicfight.world.entity.WitherGhostClone;

import java.util.Optional;
import java.util.UUID;

public class OwnableWitherGhost extends WitherGhostClone implements OwnableEntity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(
                    OwnableWitherGhost.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(
                    OwnableWitherGhost.class,
                    EntityDataSerializers.FLOAT
            );

    private static final EntityDataAccessor<Boolean> AREA_DAMAGE =
            SynchedEntityData.defineId(
                    OwnableWitherGhost.class,
                    EntityDataSerializers.BOOLEAN
            );

    public OwnableWitherGhost(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
    }

    public OwnableWitherGhost(ServerLevel level, Vec3 position, LivingEntity target) {
        this(ExtendedDatapacksEntities.OWNABLE_WITHER_GHOST.get(), level);
        this.setPos(position);
        this.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        this.setTarget(target);
    }


    @Override
    public boolean hurt(DamageSource damagesource, float damage) {
        if (!damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        return super.hurt(damagesource, damage);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (!canDamage(target)) return false;
        if (!this.getAreaDamage()) if (this.getTarget() == null || !target.equals(this.getTarget())) return false;
        var rawSupplier = ExecutionTasks.getRaw(ExecutionPolicy.RESIST, this::getEntityDamage);
        rawSupplier.ifSuccessOrElse(
                damage -> this.onSuccessfulHurtTarget(target, damage),
                error -> this.onFailureHurtTarget(target, error)
        );
        return true;
    }

    @ErrorHandled
    private float getEntityDamage() {
        return this.getDamage() == -1F ?
                (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() :
                this.getDamage();
    }

    @ErrorHandled
    private void onSuccessfulHurtTarget(Entity target, Float damage) {
        target.hurt(this.damageSources().mobAttack(this), damage);
    }

    @ErrorHandled
    private void onFailureHurtTarget(Entity target, Exception error) {
        ExtendedDatapacks.LOGGER.error(
                "[<E> - Ownable Wither Ghost] A strange error occurred while trying to extract the damage from the entity; using fallback",
                error
        );
        target.hurt(this.damageSources().mobAttack(this), 8F);
    }

    private boolean canDamage(Entity target) {
        if (this.getOwnerUUID() != null && this.getOwnerUUID().equals(target.getUUID())) return false;
        if (!(target instanceof ServerPlayer targetPlayer)) return true;
        if (!(this.level() instanceof ServerLevel serverLevel)) return false;
        if (!serverLevel.getServer().isPvpAllowed()) return false;
        if (this.getOwnerUUID() == null) return false;
        if (!(serverLevel.getEntity(this.getOwnerUUID()) instanceof ServerPlayer ownerPlayer)) return false;
        return ownerPlayer.canHarmPlayer(targetPlayer);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(DAMAGE, -1F);
        builder.define(AREA_DAMAGE, false);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public Float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public boolean getAreaDamage() {
        return this.entityData.get(AREA_DAMAGE);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setDamage(@Nullable Float damage) {
        this.entityData.set(DAMAGE, damage == null ? -1F : damage);
    }

    public void setAreaDamage(boolean areaDamage) {
        this.entityData.set(AREA_DAMAGE, areaDamage);
    }
}