package sleys.efedp.system.combat;

import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public enum ExtendedSkillSlot implements SkillSlot {
    CHARGED_ATTACK();
    final SkillCategory category;
    final int id;

    ExtendedSkillSlot() {
        this.category = ExtendedSkillCategory.CHARGED_ATTACK;
        this.id = SkillSlot.ENUM_MANAGER.assign(this);
    }

    public SkillCategory category() {
        return this.category;
    }

    public int universalOrdinal() {
        return this.id;
    }
}

