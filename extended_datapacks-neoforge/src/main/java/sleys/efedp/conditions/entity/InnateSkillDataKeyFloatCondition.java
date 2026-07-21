package sleys.efedp.conditions.entity;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import sleys.efedp.capability.data.SkillDataKeyCache;
import sleys.efedp.conditions.CommonUtilities;
import sleys.efedp.conditions.ComparisonType;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.registry.EpicFightRegistries;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;
import java.util.NoSuchElementException;

public class InnateSkillDataKeyFloatCondition extends Condition.EntityPatchCondition {
    private ResourceLocation skillFloatDataKey;
    private Float expectedValue;
    private Float tolerance;
    private ComparisonType comparisonType;

    @Override
    public InnateSkillDataKeyFloatCondition read(CompoundTag tag) {
        String keyPath = this.assertTag("skill_data_key", "string", tag, StringTag.class, CompoundTag::getString);
        if (keyPath == null) {
            throw new NoSuchElementException("Condition error: Float Skill Datakey can't be null in " + this.getClass().getSimpleName());
        }
        this.skillFloatDataKey = ResourceLocation.parse(keyPath);

        if (tag.contains("expected_value")) {
            this.expectedValue = tag.getFloat("expected_value");
        } else {
            throw new NoSuchElementException("Expected value can't be null in " + this.getClass().getSimpleName());
        }

        if (tag.contains("comparison_type")) {
            String comparisonStr = tag.getString("comparison_type");
            this.comparisonType = ComparisonType.fromString(comparisonStr);
        } else {
            this.comparisonType = ComparisonType.EQUAL;
        }

        if (tag.contains("tolerance")) {
            this.tolerance = tag.getFloat("tolerance");
        } else {
            this.tolerance = 0.001f;
        }

        return this;
    }

    @Override
    public boolean predicate(LivingEntityPatch<?> entityPatch) {
        if (!(entityPatch instanceof PlayerPatch<?> playerPatch)) return false;

        SkillContainer skillContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
        if (skillContainer == null) return false;

        SkillDataManager skillManager = skillContainer.getDataManager();
        if (skillManager == null) return false;

        DeferredHolder<SkillDataKey<?>, SkillDataKey<Float>> skillDataKey = SkillDataKeyCache.getSkillDataKey(
                skillFloatDataKey,
                Float.class
        );

        if (skillDataKey == null || !skillManager.hasData(skillDataKey)) return false;

        Float floatValue = null;
        if (skillManager.getDataValue(skillDataKey) instanceof Float primFloat) {
            floatValue = primFloat;
        }

        if (floatValue != null) {
            return CommonUtilities.compareFloatValues(floatValue, expectedValue, comparisonType, tolerance);
        }

        return false;
    }

    @Override
    public CompoundTag serializePredicate() {
        CompoundTag tag = new CompoundTag();
        if (this.skillFloatDataKey != null) {
            tag.putString("skill_data_key", this.skillFloatDataKey.toString());
        }
        if (this.expectedValue != null) {
            tag.putFloat("expected_value", this.expectedValue);
        }
        if (this.comparisonType != null) {
            tag.putString("comparison_type", this.comparisonType.getSymbol());
        }
        if (this.tolerance != null) {
            tag.putFloat("tolerance", this.tolerance);
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
                EpicFightRegistries.SKILL_DATA_KEY,
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
