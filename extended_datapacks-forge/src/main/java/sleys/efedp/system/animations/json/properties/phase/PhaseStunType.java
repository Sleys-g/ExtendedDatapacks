package sleys.efedp.system.animations.json.properties.phase;

import com.mojang.serialization.Codec;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Locale;

public class PhaseStunType {

    public static final Codec<StunType> CODEC = Codec.STRING.xmap(
            name -> switch (name.toUpperCase(Locale.ROOT)) {
                case "SHORT" -> StunType.SHORT;
                case "LONG" -> StunType.LONG;
                case "HOLD" -> StunType.HOLD;
                case "KNOCKBACK" -> StunType.KNOCKDOWN;
                case "NEUTRALIZE" -> StunType.NEUTRALIZE;
                case "FALL" -> StunType.FALL;
                default -> StunType.NONE;
            },
            stun -> stun.name().toLowerCase()
    );
}
