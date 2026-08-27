package sleys.efedp.system.innates.json.builder.data;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import sleys.sl.library.util.data.codec.EnumCodecs;

import java.util.function.Predicate;

public enum ConditionalType {
    IN_AIR("in_air", "Air Strike:",player -> !player.onGround()),
    SPRINTING("sprinting", "Dash Strike:",Player::isSprinting),
    USE_ITEM("use_item", "Hold Strike:", ConditionalType::onUseItem),
    KNEELING("kneeling", "Ground Strike:",Player::isShiftKeyDown),
    NORMAL("normal", "Impact:",Player::isAlive);

    public final String id;
    public final String tooltip;
    public final Predicate<Player> predicate;

    ConditionalType(String id, String tooltip, Predicate<Player> condition) {
        this.id = id;
        this.tooltip = tooltip;
        this.predicate = condition;
    }

    public static final Codec<ConditionalType> CODEC = EnumCodecs.byId(values(), c -> c.id);

    private static Boolean onUseItem(Player player) {
        return player.getPersistentData().getBoolean("minecraft:use_item/event/lazy");
    }
}