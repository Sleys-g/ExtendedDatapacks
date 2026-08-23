package sleys.efedp.system.innates.json.builder.data;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import sleys.sl.library.util.data.codec.EnumCodecs;

import java.util.function.Predicate;

public enum ConditionalType {
    IN_AIR("in_air", "Air Strike:",ConditionalType::onAir),
    SPRINTING("sprinting", "Dash Strike:",ConditionalType::isSprinting),
    USE_ITEM("use_item", "Hold Strike:", ConditionalType::onUseItem),
    KNEELING("kneeling", "Ground Strike:", ConditionalType::isShiftKeyDown),
    NORMAL("normal", "Impact:", ConditionalType::isNormal);

    public final String id;
    public final String tooltip;
    public final Predicate<Player> predicate;

    ConditionalType(String id, String tooltip, Predicate<Player> condition) {
        this.id = id;
        this.tooltip = tooltip;
        this.predicate = condition;
    }

    public static final Codec<ConditionalType> CODEC = EnumCodecs.byId(values(), c -> c.id);

    private static Boolean onAir(Player player) {
        return !player.onGround();
    }

    private static Boolean onUseItem(Player player) {
        return player.getPersistentData().getBoolean("minecraft:use_item/event/lazy");
    }

    private static Boolean isSprinting(Player player) {
        return player.isSprinting();
    }

    private static Boolean isShiftKeyDown(Player player) {
        return player.isShiftKeyDown();
    }

    private static Boolean isNormal(Player player) {
        return player.isAlive();
    }
}