package sleys.efedp.system.combat.charged_attacks;

import yesman.epicfight.world.capabilities.item.Style;

public enum ChargedAttackStyles implements Style {
    CHARGED_COMMON(true),
    CHARGED_ONE_HAND(true),
    CHARGED_TWO_HAND(false),
    CHARGED_OCHS(true),
    CHARGED_SHEAT(true);

    final boolean canUseOffhand;
    final int id;

    ChargedAttackStyles(boolean canUseOffhand) {
        this.id = Style.ENUM_MANAGER.assign(this);
        this.canUseOffhand = canUseOffhand;
    }

    public int universalOrdinal() {
        return this.id;
    }

    public boolean canUseOffhand() {
        return this.canUseOffhand;
    }

    private static ChargedAttackStyles[] LZStylesArray() {
        return new ChargedAttackStyles[]{
                CHARGED_COMMON,
                CHARGED_ONE_HAND,
                CHARGED_TWO_HAND,
                CHARGED_OCHS,
                CHARGED_SHEAT
        };
    }
}
