package sleys.efedp.system.innates.json.builder.values;

public record HoldableSkillValues(boolean playbackForCharging, boolean playbackForRelease,
                                  int MaxAllowedMaxChargingTicks, int MaxChargingTicks,
                                  int MinChargingTicks, boolean reduceSpeed) {}