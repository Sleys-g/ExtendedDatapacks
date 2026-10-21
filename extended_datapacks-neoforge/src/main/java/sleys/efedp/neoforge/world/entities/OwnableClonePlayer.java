package sleys.efedp.neoforge.world.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sleys.efedp.neoforge.core.events.list.entity.FlashWhitePairEvent;
import sleys.efedp.neoforge.plataform.registry.ExtendedDatapacksEntities;
import yesman.epicfight.registry.entries.EpicFightAttributes;

import java.util.Optional;
import java.util.UUID;

public class OwnableClonePlayer extends PathfinderMob implements OwnableEntity, IControlableEntityDamage {

    private static final FlashWhitePairEvent FLASH_WHITE_PAIR_EVENT = new FlashWhitePairEvent(
            8, 30, 10, false
    );

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(
                    OwnableClonePlayer.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final double FOLLOW_STRENGTH = 0.45D;
    private static final double MAX_FOLLOW_SPEED = 0.8D;
    private static final double TELEPORT_DISTANCE_SQ = 10.0D * 10.0D;

    private Vec3 spawnOffset = Vec3.ZERO;
    private long despawnAtGameTime = -1L;
    private float MULTIPLIER = 1f;

    public OwnableClonePlayer(EntityType<? extends OwnableClonePlayer> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
    }

    public OwnableClonePlayer(ServerLevel level, Vec3 offset, Vec3 position,
                              LivingEntity owner, int durationTicks) {
        this(ExtendedDatapacksEntities.OWNABLE_CLONE_PLAYER.get(), level);
        this.setOwner(owner);

        this.spawnOffset = offset;
        this.setPos(position.add(offset.yRot(-owner.getYRot() * Mth.DEG_TO_RAD)));

        this.setYRot(owner.getYRot());
        this.yRotO = owner.getYRot();
        this.setXRot(0.0F);
        this.xRotO = 0.0F;

        this.setDuration(durationTicks);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, Optional.empty());
    }

    @Override
    public boolean save(@NotNull CompoundTag tag) {
        return false;
    }

    @Override
    public boolean saveAsPassenger(@NotNull CompoundTag tag) {
        return false;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.entityData.set(OWNER_UUID, owner == null ? Optional.empty() : Optional.of(owner.getUUID()));
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        Optional<UUID> uuid = this.entityData.get(OWNER_UUID);

        if (uuid.isEmpty()) return null;
        if (!(this.level() instanceof ServerLevel serverLevel)) return null;

        Entity entity = serverLevel.getEntity(uuid.get());
        return entity instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public void setDuration(int ticks) {
        this.despawnAtGameTime = this.level().getGameTime() + ticks;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {}

    private Vec3 getTargetPosition(LivingEntity owner) {
        Vec3 rotatedOffset = this.spawnOffset.yRot(-owner.getYRot() * Mth.DEG_TO_RAD);
        return owner.position().add(rotatedOffset);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;
        LivingEntity owner = this.getOwner();

        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        if (this.despawnAtGameTime > 0 && this.level().getGameTime() >= this.despawnAtGameTime) {
            this.discard();
            return;
        }

        Vec3 targetPosition = this.getTargetPosition(owner);
        if (this.distanceToSqr(targetPosition) > TELEPORT_DISTANCE_SQ) {
            this.teleportTo(targetPosition.x, targetPosition.y, targetPosition.z);
            this.setDeltaMovement(Vec3.ZERO);

            FLASH_WHITE_PAIR_EVENT.executeEvent(this);
            this.triggerFlashWhiteAndParticles();
        }

        if (!owner.isShiftKeyDown()) this.tickOwnerMovement();
        this.tickOwnerRotation();
        this.tickOwnerState();
        this.tickOwnerEquipment();
    }

    public void triggerFlashWhiteAndParticles() {
        if (this.level().isClientSide) return;
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.PORTAL,
                    this.getX(), this.getY() + (this.getBbHeight() / 2.0D), this.getZ(),
                    15,
                    0.3D, 0.5D, 0.3D,
                    0.05D
            );
        }
    }

    protected void tickOwnerMovement() {
        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        Vec3 targetPosition = this.getTargetPosition(owner);
        Vec3 difference = targetPosition.subtract(this.position());
        Vec3 horizontalDifference = new Vec3(difference.x, 0.0D, difference.z);

        Vec3 currentMovement = this.getDeltaMovement();
        Vec3 velocity = horizontalDifference.scale(FOLLOW_STRENGTH);

        if (velocity.lengthSqr() > MAX_FOLLOW_SPEED * MAX_FOLLOW_SPEED) {
            velocity = velocity.normalize().scale(MAX_FOLLOW_SPEED);
        }

        double yVelocity = currentMovement.y;
        if (!this.onGround()) {
            yVelocity = Mth.clamp(difference.y * FOLLOW_STRENGTH, -MAX_FOLLOW_SPEED, MAX_FOLLOW_SPEED);
        }

        this.setDeltaMovement(velocity.x, yVelocity, velocity.z);
    }

    protected void tickOwnerRotation() {
        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        float ownerYaw = owner.getYRot();
        this.setYRot(ownerYaw);
        this.yRotO = ownerYaw;

        this.setXRot(0.0F);
        this.xRotO = 0.0F;
        this.yHeadRot = ownerYaw;
        this.yHeadRotO = ownerYaw;
    }

    protected void tickOwnerState() {
        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        this.setPose(owner.getPose());
        this.setShiftKeyDown(owner.isShiftKeyDown());
        this.setSprinting(owner.isSprinting());
        this.setSwimming(owner.isSwimming());
    }

    protected void tickOwnerEquipment() {
        LivingEntity owner = this.getOwner();
        if (owner == null) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = owner.getItemBySlot(slot);
            this.setItemSlot(slot, stack.copy());
            this.setDropChance(slot, 0.0F);
        }
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        return true;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (!this.canDamage(target)) return false;
        return super.doHurtTarget(target);
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

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.MOVEMENT_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS)
                .add(Attributes.ENTITY_INTERACTION_RANGE)
                .add(EpicFightAttributes.WEIGHT)
                .add(EpicFightAttributes.ARMOR_NEGATION)
                .add(EpicFightAttributes.IMPACT)
                .add(EpicFightAttributes.MAX_STRIKES);
    }

    @Override
    public void entityHurtEvent(LivingDamageEvent.Pre event) {
        var damage = event.getNewDamage() * this.getMultiplier();
        event.setNewDamage(damage);
    }

    public float getMultiplier() {
        return this.MULTIPLIER;
    }

    public void setMultiplier(float multiplier) {
        this.MULTIPLIER = multiplier;
    }
}