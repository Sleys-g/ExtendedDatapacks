package sleys.efedp.client.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class EpicFightEDPClientConfig {

    public static final ForgeConfigSpec EDP_CLIENT;

    public static final ForgeConfigSpec.IntValue PARTICLE_VIEWER;
    public static final ForgeConfigSpec.BooleanValue SEE_PASSIVE_PARTICLES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Extended Datapacks - Client");

        SEE_PASSIVE_PARTICLES = builder
                .comment("Determines whether you want to see the passive particles of weapons")
                .define("seePassiveWeaponsParticle", true)
        ;

        PARTICLE_VIEWER = builder
                .comment("Square of the distance between two viewing entities in which particles can be generated")
                .defineInRange("particleViewer", 9025, 4900, 10000)
        ;

        builder.pop();
        EDP_CLIENT = builder.build();
    }


    public static boolean getSeePassiveParticles() {
        return SEE_PASSIVE_PARTICLES.get();
    }

    public static int getParticleViewer() {
        return PARTICLE_VIEWER.get();
    }
}
