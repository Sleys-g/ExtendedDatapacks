package sleys.efedp.system.combat;

import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public enum ExtendedSkillSlot implements SkillSlot {
    CHARGED_ATTACK(ExtendedSkillCategory.CHARGED_ATTACK);
    final SkillCategory category;
    final int id;

    ExtendedSkillSlot(SkillCategory category) {
        this.category = category;
        this.id = SkillSlot.ENUM_MANAGER.assign(this);
    }

    public SkillCategory category() {
        return this.category;
    }

    public int universalOrdinal() {
        return this.id;
    }
}

