package sleys.efedp.neoforge.world.entities;

import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sleys.efedp.neoforge.registry.ExtendedDatapacksEntities;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.registry.entries.EpicFightAttributes;

import java.util.Optional;
import java.util.UUID;

public class OwnableAnimatedPlayer extends PathfinderMob implements OwnableEntity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID =
            SynchedEntityData.defineId(
                    OwnableAnimatedPlayer.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Boolean> NEED_DISCARD =
            SynchedEntityData.defineId(
                    OwnableAnimatedPlayer.class,
                    EntityDataSerializers.BOOLEAN
            );

    private AssetAccessor<? extends StaticAnimation> animation;

    public OwnableAnimatedPlayer(EntityType<? extends OwnableAnimatedPlayer> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
    }

    public OwnableAnimatedPlayer(ServerLevel level, Vec3 offset, Vec3 position,
                                 LivingEntity owner, @Nullable LivingEntity target,
                                 AssetAccessor<? extends StaticAnimation> animation) {
        this(ExtendedDatapacksEntities.OWNABLE_ANIMATED_PLAYER.get(), level);

        this.setOwner(owner);
        this.setPos(position.add(offset.yRot(-owner.getYRot() * Mth.DEG_TO_RAD)));
        this.setTarget(target);
        this.animation = animation;

        this.updateRotation();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);

        builder.define(OWNER_UUID, Optional.empty());
        builder.define(NEED_DISCARD, false);
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
        this.entityData.set(OWNER_UUID, owner == null
                ? Optional.empty()
                : Optional.of(owner.getUUID())
        );
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setNeedDiscard(boolean b) {
        this.entityData.set(NEED_DISCARD, b);
    }

    public boolean getNeedDiscord() {
        return this.entityData.get(NEED_DISCARD);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        Optional<UUID> uuid = this.entityData.get(OWNER_UUID);

        if (uuid.isEmpty()) return null;
        if (!(this.level() instanceof ServerLevel serverLevel)) return null;
        var entity = serverLevel.getEntity(uuid.get());

        return entity instanceof LivingEntity livingEntity
                ? livingEntity
                : null;
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public AssetAccessor<? extends StaticAnimation> getAnimation() {
        return animation;
    }

    private void updateRotation() {
        var owner = this.getOwner();
        if (owner == null) return;
        var target = this.getTarget();

        if (target != null && target.isAlive()) {
            this.lookAt(EntityAnchorArgument.Anchor.EYES, target.position().add(0.0, target.getEyeHeight(), 0.0));
            this.setYHeadRot(this.getYRot());
            this.setYBodyRot(this.getYRot());
            return;
        }

        float yaw = owner.getYHeadRot();
        float pitch = owner.getXRot();

        this.setYRot(yaw);
        this.setXRot(pitch);

        this.setYHeadRot(yaw);
        this.setYBodyRot(yaw);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;
        var owner = this.getOwner();

        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        this.updateRotation();
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
        var ownerUUID = this.getOwnerUUID();
        if (ownerUUID != null && ownerUUID.equals(target.getUUID())) return false;
        if (!(target instanceof ServerPlayer targetPlayer)) return true;
        if (!(this.level() instanceof ServerLevel serverLevel)) return false;
        if (!serverLevel.getServer().isPvpAllowed()) return false;
        if (ownerUUID == null) return false;
        if (!(serverLevel.getEntity(ownerUUID) instanceof ServerPlayer ownerPlayer)) return false;
        return ownerPlayer.canHarmPlayer(targetPlayer);
    }

    public void copySlots() {
        var owner = this.getOwner();
        if (owner == null) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = owner.getItemBySlot(slot);

            this.setItemSlot(slot, stack.copy());
            this.setDropChance(slot, 0.0F);
        }
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
}