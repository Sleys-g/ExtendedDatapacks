package sleys.efedp.neoforge.system.skills.weapon.innate.builder.values;

public record HoldableSkillValues(boolean playbackForCharging, boolean playbackForRelease,
                                  int MaxAllowedMaxChargingTicks, int MaxChargingTicks,
                                  int MinChargingTicks, boolean reduceSpeed) {}