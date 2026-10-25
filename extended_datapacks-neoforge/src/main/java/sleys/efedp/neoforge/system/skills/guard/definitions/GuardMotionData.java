package sleys.efedp.neoforge.system.skills.guard.definitions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public record GuardMotionData(String type, Map<String, Either<String, List<String>>> styleData) {

    private static final Codec<Either<String, List<String>>> VALUE_CODEC = Codec.either(Codec.STRING, Codec.STRING.listOf());

    private static final Codec<GuardMotionData> OBJECT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("type")
                            .forGetter(GuardMotionData::type),

                    Codec.unboundedMap(Codec.STRING, VALUE_CODEC)
                            .fieldOf("per_style")
                            .forGetter(GuardMotionData::styleData)
            ).apply(instance, GuardMotionData::new));

    public static final Codec<GuardMotionData> CODEC =
            Codec.either(Codec.STRING, OBJECT_CODEC).xmap(
                    either -> either.map(animation -> new GuardMotionData(
                            "single", Map.of("return", Either.left(animation)
                            )),
                            Function.identity()
                    ),
                    data -> {
                        if ("single".equals(data.type()) && data.styleData().size() == 1 && data.styleData().containsKey("return")) {
                            var value = data.styleData().get("return");
                            if (value.left().isPresent()) return Either.left(value.left().get());
                        }

                        return Either.right(data);
                    }
            );

    public GuardMotionData {
        styleData = Map.copyOf(styleData);
    }

    public Set<String> getDeclaredStyles() {
        return styleData.keySet();
    }

    public boolean isPerStyle() {
        return styleData.size() > 1 || !styleData.containsKey("return");
    }

    public boolean hasStyle(String style) {
        return styleData.containsKey(style);
    }

    public boolean isMulti() {
        return "multi".equals(type);
    }

    @Nullable
    public String getAnimationForStyle(String style) {
        return styleData.getOrDefault(style, Either.left("")).left()
                .filter(value -> !value.isEmpty())
                .orElse(null);
    }

    @Nullable
    public List<String> getAnimationsForStyle(String style) {
        return styleData.getOrDefault(style, Either.right(List.of())).right()
                .orElse(null);
    }

    @Nullable
    public String getReturnAnimation() {
        return getAnimationForStyle("return");
    }

    @Nullable
    public List<String> getReturnAnimations() {
        return getAnimationsForStyle("return");
    }
}
