package sleys.efedp.system.animations.json.properties.phase;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;

import java.util.Optional;

public record ColliderArray(Optional<Integer> arrayLength,
                            Optional<Vec3> vertex,
                            Optional<Vec3> center) {

    public static MapCodec<ColliderArray> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.INT.optionalFieldOf("array_length").forGetter(ColliderArray::arrayLength),
                        Vec3.CODEC.optionalFieldOf("vertex").forGetter(ColliderArray::vertex),
                        Vec3.CODEC.optionalFieldOf("center").forGetter(ColliderArray::center)
                ).apply(instance, ColliderArray::new)
        );
    }

    public static MultiOBBCollider getMultiOBBCollider(Optional<ColliderArray> optionalKey) {
        if (optionalKey.isEmpty()) return null;
        var key = optionalKey.get();

        int arrayLength = key.arrayLength.orElse(1);
        Vec3 vertex = key.vertex.orElse(Vec3.ZERO);
        Vec3 center = key.center.orElse(Vec3.ZERO);
        if (isEmpty(vertex, center)) {
            return null;
        }
        return new MultiOBBCollider(arrayLength, vertex.x, vertex.y, vertex.z, center.x, center.y, center.z);
    }

    public static OBBCollider getOBBCollider(Optional<ColliderArray> optionalKey) {
        if (optionalKey.isEmpty()) return null;
        var key = optionalKey.get();

        Vec3 vertex = key.vertex.orElse(Vec3.ZERO);
        Vec3 center = key.center.orElse(Vec3.ZERO);

        if (isEmpty(vertex, center)) {
            return null;
        }
        return new OBBCollider(vertex.x, vertex.y, vertex.z, center.x, center.y, center.z);
    }

    private static boolean isEmpty(Vec3 vertex, Vec3 center) {
        return vertex.equals(Vec3.ZERO) && center.equals(Vec3.ZERO);
    }
}
