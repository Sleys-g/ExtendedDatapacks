package sleys.efedp.system.combat;

import yesman.epicfight.skill.SkillCategory;

public enum ExtendedSkillCategory implements SkillCategory {
    CHARGED_ATTACK();
    final int id;

    ExtendedSkillCategory() {
        this.id = SkillCategory.ENUM_MANAGER.assign(this);
    }

    public boolean shouldSave() {
        return true;
    }

    public boolean shouldSynchronize() {
        return false;
    }

    public boolean learnable() {
        return false;
    }

    public int universalOrdinal() {
        return this.id;
    }
}
