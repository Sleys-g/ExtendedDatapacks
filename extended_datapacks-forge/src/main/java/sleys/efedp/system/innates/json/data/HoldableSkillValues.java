package sleys.efedp.system.innates.json.data;

public record HoldableSkillValues(boolean playbackForCharging, boolean playbackForRelease,
                                  int MaxAllowedMaxChargingTicks, int MaxChargingTicks,
                                  int MinChargingTicks, boolean reduceSpeed) {}
