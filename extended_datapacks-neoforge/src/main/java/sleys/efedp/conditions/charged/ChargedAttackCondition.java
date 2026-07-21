package sleys.efedp.conditions.charged;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.system.combat.charged_attacks.ChargedAttack;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;
import java.util.NoSuchElementException;

public class ChargedAttackCondition extends Condition.EntityPatchCondition {
    private Boolean offHandCapability;

    @Override
    public ChargedAttackCondition read(CompoundTag tag) {
        if (tag.contains("allow_two_hand_compatibility")) {
            this.offHandCapability = tag.getBoolean("allow_two_hand_compatibility");
        } else {
            throw new NoSuchElementException("Capability Hand can't be null in " + this.getClass().getSimpleName());
        }
        return this;
    }

    @Override
    public boolean predicate(LivingEntityPatch<?> entityPatch) {
        if (entityPatch instanceof PlayerPatch<?> playerPatch) {
            final var player = playerPatch.getOriginal();
            final var itemCapabilityMain = playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND);
            final var itemCapabilityOff = playerPatch.getHoldingItemCapability(InteractionHand.OFF_HAND);
            if (itemCapabilityMain == null) return false;

            final var mainHandCategory = itemCapabilityMain.getWeaponCategory();
            final var offHandCategory = itemCapabilityOff != null ? itemCapabilityOff.getWeaponCategory() : null;
            if (ChargedAttack.getPressedCastKey(player)) {
                boolean sameCategory = mainHandCategory.equals(offHandCategory);
                if (offHandCapability && sameCategory) {
                    return true;
                }
                return !offHandCapability && !sameCategory;
            }
        }
        return false;
    }

    @Override
    public CompoundTag serializePredicate() {
        CompoundTag tag = new CompoundTag();
        if (this.offHandCapability != null) {
            tag.putBoolean("allow_two_hand_compatibility", this.offHandCapability);
        }
        return tag;
    }

    @OnlyIn(Dist.CLIENT)
    public List<Condition.ParameterEditor> getAcceptingParameters(Screen screen) {
        AbstractWidget popupBox = new PopupBox.RegistryPopupBox<>(
                screen,
                screen.getMinecraft().font,
                0, 0, 0, 0,
                null, null,
                Component.literal("THIS DOES NOTHING; IT JUST PREVENTS THE CRASH"),
                EpicFightRegistries.SKILL_DATA_KEY,
                null
        );

        return List.of(
                Condition.ParameterEditor.of(
                        (rl) -> StringTag.valueOf(rl.toString()),
                        (tag) -> ResourceLocation.parse(ParseUtil.nullOrToString(tag, Tag::getAsString)),
                        popupBox
                )
        );
    }
}
