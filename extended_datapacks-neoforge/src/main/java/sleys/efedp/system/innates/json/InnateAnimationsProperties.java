package sleys.efedp.system.innates.json;

import com.google.gson.JsonObject;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Locale;

public record InnateAnimationsProperties(float maxStrikes,
                                         float damageMultiplier,
                                         float armorNegation,
                                         float impact,
                                         String stunType,
                                         boolean extraDamage) {

    public StunType getParsedStuntype() {
        return switch (stunType.toUpperCase(Locale.ROOT)) {
            case "SHORT" -> StunType.SHORT;
            case "LONG" -> StunType.LONG;
            case "HOLD" -> StunType.HOLD;
            case "KNOCKBACK" -> StunType.KNOCKDOWN;
            case "NEUTRALIZE" -> StunType.NEUTRALIZE;
            case "FALL" -> StunType.FALL;
            default -> StunType.NONE;
        };
    }

    public static InnateAnimationsProperties parseProperties(JsonObject obj) {

        float maxStrikes = obj.has("max_strikes") ? obj.get("max_strikes").getAsFloat() : 1.0F;
        float damageMultiplier = obj.has("damage_multiplier") ? obj.get("damage_multiplier").getAsFloat() : 1.0F;
        float armorNegation = obj.has("armor_negation") ? obj.get("armor_negation").getAsFloat() : 0F;
        float impact = obj.has("impact") ? obj.get("impact").getAsFloat() : 1.0F;
        String stunType = obj.has("stun_type") ? obj.get("stun_type").getAsString() : "NONE";
        boolean extraDamage = obj.has("extra_damage") && obj.get("extra_damage").getAsBoolean();

        return new InnateAnimationsProperties(
                maxStrikes,
                damageMultiplier,
                armorNegation,
                impact,
                stunType,
                extraDamage
        );
    }
}