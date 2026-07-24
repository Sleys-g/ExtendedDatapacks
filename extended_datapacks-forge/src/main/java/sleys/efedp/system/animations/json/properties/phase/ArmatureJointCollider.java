package sleys.efedp.system.animations.json.properties.phase;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.sl.library.exceptions.RegistryObjectException;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;

import java.util.Optional;

public record ArmatureJointCollider(String joint, Optional<ColliderArray> collider) {

    public static final MapCodec<ArmatureJointCollider> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("joint").forGetter(ArmatureJointCollider::joint),
                    ColliderArray.codec().codec().optionalFieldOf("collider").forGetter(ArmatureJointCollider::collider)
            ).apply(instance, ArmatureJointCollider::new)
    );

    public AttackAnimation.JointColliderPair resolve(ArmatureType armatureType) {
        Joint joint = armatureType.accessor.get().searchJointByName(this.joint);
        if (joint == null) throw new RegistryObjectException("[Armature Joint Collider] The assigned joint does not exist or is out of range; this occurs for the ID: " + this.joint);
        if (collider.isPresent()) {
            return collider.get().arrayLength().isEmpty() ?
                    AttackAnimation.JointColliderPair.of(joint, ColliderArray.getOBBCollider(collider)) :
                    AttackAnimation.JointColliderPair.of(joint, ColliderArray.getMultiOBBCollider(collider));
        }

        return AttackAnimation.JointColliderPair.of(joint, null);
    }
}