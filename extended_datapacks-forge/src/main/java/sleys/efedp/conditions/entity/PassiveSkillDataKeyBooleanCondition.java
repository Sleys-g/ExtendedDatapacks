package sleys.efedp.conditions.entity;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.data.conditions.Condition.EntityPatchCondition;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;
import java.util.NoSuchElementException;

public class PassiveSkillDataKeyBooleanCondition extends EntityPatchCondition {
    private ResourceLocation skillBooleanDataKey;
    private Boolean expectedValue;

    @Override
    public PassiveSkillDataKeyBooleanCondition read(CompoundTag tag) {
        String keyPath = this.assertTag("skill_data_key", "string", tag, StringTag.class, CompoundTag::getString);
        if (keyPath == null) {
            throw new NoSuchElementException("Condition error: Boolean Skill Datakey can't be null in " + this.getClass().getSimpleName());
        }
        this.skillBooleanDataKey = ResourceLocation.parse(keyPath);
        if (tag.contains("expected_value")) {
            this.expectedValue = tag.getBoolean("expected_value");
        } else {
            throw new NoSuchElementException("Expected value can't be null in " + this.getClass().getSimpleName());
        }

        return this;
    }

    @Override
    public boolean predicate(LivingEntityPatch<?> entityPatch) {
        if (entityPatch instanceof PlayerPatch<?> playerPatch) {
            SkillContainer skillContainer = playerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
            if (skillContainer == null) return false;

            SkillDataManager skillManager = skillContainer.getDataManager();
            if (skillManager == null) return false;

            SkillDataKey<?> key = SkillDataKeys.REGISTRY.get().getValue(skillBooleanDataKey);
            if (key == null) return false;

            if (skillManager.hasData(key)) {
                Object value = skillManager.getDataValue(key);
                if (value instanceof Boolean boolValue) {
                    return expectedValue.equals(boolValue);
                }
            }
        }
        return false;
    }

    @Override
    public CompoundTag serializePredicate() {
        CompoundTag tag = new CompoundTag();
        if (this.skillBooleanDataKey != null) {
            tag.putString("skill_data_key", this.skillBooleanDataKey.toString());
        }
        if (this.expectedValue != null) {
            tag.putBoolean("expected_value", this.expectedValue);
        }
        return tag;
    }

    @OnlyIn(Dist.CLIENT)
    public List<ParameterEditor> getAcceptingParameters(Screen screen) {
        AbstractWidget popupBox = new PopupBox.RegistryPopupBox<>(
                screen,
                screen.getMinecraft().font,
                0, 0, 0, 0,
                null, null,
                Component.literal("Skill Data Key"),
                SkillDataKeys.REGISTRY.get(),
                null
        );

        return List.of(
                ParameterEditor.of(
                        (rl) -> StringTag.valueOf(rl.toString()),
                        (tag) -> ResourceLocation.parse(ParseUtil.nullOrToString(tag, Tag::getAsString)),
                        popupBox
                )
        );
    }
}
