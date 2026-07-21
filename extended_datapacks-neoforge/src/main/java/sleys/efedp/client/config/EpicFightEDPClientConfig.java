package sleys.efedp.client.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class EpicFightEDPClientConfig {

    public static final ModConfigSpec EDP_CLIENT;

    public static final ModConfigSpec.IntValue PARTICLE_VIEWER;
    public static final ModConfigSpec.BooleanValue SEE_PASSIVE_PARTICLES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
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
