package sleys.efedp.forge.conditions.entity;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.PopupBox;
import yesman.epicfight.client.gui.datapack.widgets.ResizableComponent;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

public class HasSkillCondition extends Condition.EntityPatchCondition {
    private String skillRegistry;

    @Override
    public HasSkillCondition read(CompoundTag tag) {
        String keyPath = this.assertTag("skill", "string", tag, StringTag.class, CompoundTag::getString);
        if (keyPath == null) {
            throw new NoSuchElementException("Condition error: Skill can't be null in " + this.getClass().getSimpleName());
        }

        this.skillRegistry = keyPath;
        return this;
    }

    @Override
    public boolean predicate(LivingEntityPatch<?> entityPatch) {
        if (!(entityPatch instanceof PlayerPatch<?> playerPatch)) return false;

        var skill = SkillManager.getSkill(this.skillRegistry);
        if (skill == null) return false;

        Optional<SkillContainer> skillContainerFor = playerPatch.getSkillContainerFor(skill);
        return skillContainerFor.map(skillContainer -> {
            var targetSkill = skillContainer.getSkill();
            if (targetSkill == null) return false;

            var skillRegistry = targetSkill.getRegistryName();
            return skillRegistry != null;
        }).orElse(false);
    }

    @Override
    public CompoundTag serializePredicate() {
        CompoundTag tag = new CompoundTag();
        tag.putString("skill", this.skillRegistry);
        return tag;
    }

    @OnlyIn(Dist.CLIENT)
    public List<ParameterEditor> getAcceptingParameters(Screen screen) {
        AbstractWidget popupBox = new PopupBox.RegistryPopupBox<>(
                screen,
                screen.getMinecraft().font,
                0, 0, 0, 0,
                null, null,
                Component.literal("skill"),
                SkillManager.getSkillRegistry(),
                null
        );

        return List.of(ParameterEditor.of(
                skill -> StringTag.valueOf(skill.toString()),
                tag -> SkillManager.getSkill(ParseUtil.nullOrToString(tag, Tag::getAsString)), popupBox)
        );
    }
}