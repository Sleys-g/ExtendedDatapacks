package sleys.efedp.forge.system.skills.weapon.innate.values;

public record HoldableSkillValues(boolean playbackForCharging, boolean playbackForRelease,
                                  int MaxAllowedMaxChargingTicks, int MaxChargingTicks,
                                  int MinChargingTicks, boolean reduceSpeed) {}