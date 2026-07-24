package sleys.efedp.system.combat.charged_attacks;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.client.keybinding.EDPCombatKeyBinding;
import sleys.efedp.config.EpicFightEDPConfig;
import sleys.efedp.system.combat.ExtendedSkillCategory;
import sleys.efedp.registry.ExtendedDatapacksRegistrySkills;
import sleys.sl.epicfight.events.EFPlayerAnimationEvent;
import sleys.sl.epicfight.events.EFPlayerAttackSpeedEvent;
import sleys.sl.epicfight.skills.extender.ExtendedPassiveSkill;
import sleys.sl.epicfight.skills.interfaces.combat.IOnAnimationPhaseEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.combat.IOnAttackSpeedEFSkillEvent;
import sleys.sl.epicfight.skills.interfaces.combat.IOnLivingDamageEFSkillEvent;
import sleys.sl.library.annotations.ErrorHandled;
import sleys.sl.library.execution.policy.ExecutionPolicy;
import sleys.sl.library.execution.policy.ExecutionTasks;
import sleys.sl.library.network.sync.TagSyncSender;
import yesman.epicfight.registry.entries.EpicFightAttributes;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.Style;

public class ChargedAttack extends ExtendedPassiveSkill implements
        IOnLivingDamageEFSkillEvent, IOnAnimationPhaseEFSkillEvent, IOnAttackSpeedEFSkillEvent {

    private static final String IsAttackAnimation = "lzm_epicfight.charged_attack.key.attack_animation";
    private static final String LastChargedState = "lzm.last_charged_state";
    private static final String PressedTimeKey = "lzm_epicfight.charged_attack.key.time";
    private static final String PressedCastKey = "lzm_epicfight.charged_attack.key.cast";
    private static final String CastDurationKey = "lzm_epicfight.charged_attack.key.cast_duration";
    private static final int MaxTickChargedEvent = 10;
    private static final int CastPersistTicks = 10;
    private static final float CostStamina = 2.0F;

    public static boolean getPressedCastKey(Player player) {
        return player.getPersistentData().getBoolean(PressedCastKey);
    }

    public static SkillBuilder<?> createChargedAttackBuilder() {
        return new SkillBuilder<>(ChargedAttack::new)
                .setCategory(ExtendedSkillCategory.CHARGED_ATTACK)
                .setActivateType(ActivateType.ONE_SHOT)
                .setResource(Resource.NONE);
    }

    public ChargedAttack(SkillBuilder<?> builder) {
        super(builder);
    }

    @Override
    public String putCaller() {
        return ExtendedDatapacks.MODID;
    }

    @Override
    public ResourceLocation putSkill() {
        return ExtendedDatapacksRegistrySkills.CHARGED_ATTACK.getId();
    }

    @Override
    public void onAttackAnimationEvent(EFPlayerAnimationEvent.AnimationEvent attackEvent, SkillContainer container) {
        final PlayerPatch<?> playerPatch = container.getExecutor();
        final var playerStyle = playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getStyle(playerPatch);
        for (Style handStyle : ChargedAttackStyles.values()) {
            if (playerStyle.equals(handStyle)) {
                final var actuallyStamina = playerPatch.getStamina();
                final var newStamina = actuallyStamina - getCorrectlyStaminaCost(playerPatch) < 0 ?
                        0 : actuallyStamina - getCorrectlyStaminaCost(playerPatch);
                playerPatch.setStamina(newStamina);
            }
        }
    }

    @Override
    public void onDealPrePlayerDamageEvent(LivingDamageEvent.Pre preEvent, SkillContainer container) {
        final PlayerPatch<?> playerPatch = container.getExecutor();
        final var playerStyle = playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getStyle(playerPatch);
        for (Style handStyle : ChargedAttackStyles.values()) {
            if (playerStyle.equals(handStyle)) {
                final var actuallyDamage = preEvent.getNewDamage();
                final var newDamage = actuallyDamage * 1.20F; // +20% De Daño de Ataque
                preEvent.setNewDamage(newDamage);
            }
        }
    }

    @Override
    public void onAttackSpeed(EFPlayerAttackSpeedEvent.EFAttackSpeedEvent event, SkillContainer container) {
        final PlayerPatch<?> playerPatch = container.getExecutor();
        final var playerStyle = playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getStyle(playerPatch);
        for (Style handStyle : ChargedAttackStyles.values()) {
            if (playerStyle.equals(handStyle)) {
                final var actuallySpeed = event.getAttackSpeed();
                final var newSpeed = actuallySpeed * 0.85F; // -15% De Velocidad de Ataque
                event.setAttackSpeed(newSpeed);
            }
        }
    }

    @Override
    public void ServerThreadSkill(SkillContainer container, PlayerPatch<?> patchPlayer,
                                  ServerPlayer player, ServerLevel world, CompoundTag persistentData) {
        if (!(patchPlayer instanceof ServerPlayerPatch serverPlayerPatch)) return;

        TagSyncSender.sendBoolean(
                TagSyncSender.SyncMethod.STC,
                player,
                PressedCastKey,
                getPressedCastKey(player)
        );

        boolean chargedNow = ChargedAttack.getPressedCastKey(player);
        boolean chargedBefore = persistentData.getBoolean(LastChargedState);
        if (chargedNow == chargedBefore) {
            return;
        }

        persistentData.putBoolean(LastChargedState, chargedNow);
        ItemStack mainHandItem = player.getMainHandItem();
        var optionalCapability = EpicFightCapabilities.getItemCapability(mainHandItem);
        optionalCapability.ifPresent(capability ->
                serverPlayerPatch.updateHeldItem(
                        capability,
                        capability,
                        mainHandItem,
                        mainHandItem,
                        InteractionHand.MAIN_HAND
                )
        );
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void ClientThreadSkill(SkillContainer container, PlayerPatch<?> patchPlayer, Player player, Level world, InteractionHand hand) {
        final var caster = patchPlayer.getOriginal();
        final var data = caster.getPersistentData();
        final var allowIs = patchPlayer.getStamina();
        final boolean cast = data.getBoolean(PressedCastKey);
        int castDuration = data.getInt(CastDurationKey);
        int time = data.getInt(PressedTimeKey);

        if (allowIs > getCorrectlyStaminaCost(patchPlayer)) {
            if (EDPCombatKeyBinding.chargedAttackKeyBinding.isDown()) {
                time++;
                if (time > MaxTickChargedEvent) time = MaxTickChargedEvent;
                TagSyncSender.sendInt(
                        TagSyncSender.SyncMethod.SAVE_AND_CTS,
                        player, PressedTimeKey, time
                );

                if (time == MaxTickChargedEvent && !cast) {
                    TagSyncSender.sendBoolean(
                            TagSyncSender.SyncMethod.SAVE_AND_CTS,
                            player, PressedCastKey, true
                    );
                }
            } else {
                if (cast) {
                    castDuration++;
                    if (castDuration >= CastPersistTicks) {
                        TagSyncSender.sendInt(
                                TagSyncSender.SyncMethod.SAVE_AND_CTS,
                                player, CastDurationKey, 0
                        );

                        TagSyncSender.sendBoolean(
                                TagSyncSender.SyncMethod.SAVE_AND_CTS,
                                player, PressedCastKey, false
                        );
                    } else {
                        TagSyncSender.sendInt(
                                TagSyncSender.SyncMethod.SAVE_AND_CTS,
                                player, CastDurationKey, castDuration
                        );
                    }
                }

                if (time != 0) {
                    TagSyncSender.sendInt(
                            TagSyncSender.SyncMethod.SAVE_AND_CTS,
                            player, PressedTimeKey, 0
                    );
                }
            }
        } else if (cast) {
            TagSyncSender.sendInt(
                    TagSyncSender.SyncMethod.SAVE_AND_CTS,
                    player, CastDurationKey, 0
            );

            TagSyncSender.sendBoolean(
                    TagSyncSender.SyncMethod.SAVE_AND_CTS,
                    player, PressedCastKey, false
            );
        }
    }

    private float getCorrectlyStaminaCost(PlayerPatch<?> patch) {
        return ExecutionTasks.getAndFallback(
                ExecutionPolicy.RESIST,
                () -> calculateStaminaCost(patch),
                0F
        );
    }

    @ErrorHandled
    private float calculateStaminaCost(PlayerPatch<?> patch) {
        if (EpicFightEDPConfig.getUseStaminaInChargedAttacks()) {
            if (EpicFightEDPConfig.getUseWeightInChargedAttacks()) {
                LivingEntity entity = patch.getOriginal();
                AttributeInstance weightAttr = entity.getAttribute(EpicFightAttributes.WEIGHT);
                float weight = weightAttr != null ? (float) weightAttr.getValue() != 0.0F ? (float) weightAttr.getValue() : 1F : 1F;
                return (CostStamina * (1.0F + weight * 0.05F) / EpicFightEDPConfig.getWeightValueInChargedAttacks()) / 1.5F;
            } else {
                return CostStamina;
            }
        } else {
            return 0;
        }
    }
}

