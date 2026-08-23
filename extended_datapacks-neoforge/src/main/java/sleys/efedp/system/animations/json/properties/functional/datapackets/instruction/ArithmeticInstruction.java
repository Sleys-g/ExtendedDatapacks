package sleys.efedp.system.animations.json.properties.functional.datapackets.instruction;

import com.mojang.serialization.Codec;
import sleys.efedp.ExtendedDatapacks;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum ArithmeticInstruction {
    SET, ADD, SUBTRACT, MULTIPLY, DIVIDE, MIN, MAX, MOD;
    public static final Codec<ArithmeticInstruction> CODEC = EnumCodecs.byId(values(), Enum::name);

    public int apply(int current, int operand, String dataId) {
        return switch (this) {
            case SET -> operand;
            case ADD -> current + operand;
            case SUBTRACT -> current - operand;
            case MULTIPLY -> current * operand;
            case DIVIDE -> operand == 0 ? warnZero(dataId, current) : current / operand;
            case MOD -> operand == 0 ? warnZero(dataId, current) : current % operand;
            case MIN -> Math.min(current, operand);
            case MAX -> Math.max(current, operand);
        };
    }

    public long apply(long current, long operand, String dataId) {
        return switch (this) {
            case SET -> operand;
            case ADD -> current + operand;
            case SUBTRACT -> current - operand;
            case MULTIPLY -> current * operand;
            case DIVIDE -> operand == 0L ? warnZero(dataId, current) : current / operand;
            case MOD -> operand == 0L ? warnZero(dataId, current) : current % operand;
            case MIN -> Math.min(current, operand);
            case MAX -> Math.max(current, operand);
        };
    }

    public float apply(float current, float operand, String dataId) {
        return switch (this) {
            case SET -> operand;
            case ADD -> current + operand;
            case SUBTRACT -> current - operand;
            case MULTIPLY -> current * operand;
            case DIVIDE -> operand == 0f ? warnZero(dataId, current) : current / operand;
            case MOD -> operand == 0f ? warnZero(dataId, current) : current % operand;
            case MIN -> Math.min(current, operand);
            case MAX -> Math.max(current, operand);
        };
    }

    public double apply(double current, double operand, String dataId) {
        return switch (this) {
            case SET -> operand;
            case ADD -> current + operand;
            case SUBTRACT -> current - operand;
            case MULTIPLY -> current * operand;
            case DIVIDE -> operand == 0d ? warnZero(dataId, current) : current / operand;
            case MOD -> operand == 0d ? warnZero(dataId, current) : current % operand;
            case MIN -> Math.min(current, operand);
            case MAX -> Math.max(current, operand);
        };
    }

    public byte apply(byte current, byte operand, String dataId) {
        int result = switch (this) {
            case SET -> (int) operand;
            case ADD -> (int) current + (int) operand;
            case SUBTRACT -> (int) current - (int) operand;
            case MULTIPLY -> (int) current * (int) operand;
            case DIVIDE -> (int) operand == 0 ? warnZero(dataId, (int) current) : (int) current / (int) operand;
            case MOD -> (int) operand == 0 ? warnZero(dataId, (int) current) : (int) current % (int) operand;
            case MIN -> Math.min(current, operand);
            case MAX -> Math.max(current, operand);
        };
        return clampToByte(result, dataId);
    }

    private  <T extends Number> T warnZero(String dataId, T current) {
        ExtendedDatapacks.LOGGER.warn(
                "[Arithmetic Instruction] The operation of type '{}' failed for the data '{}'; Since its operator is invalid because it's 0, the current value is returned, which is: {}...",
                dataId, this, current
        );
        return current;
    }

    private static byte clampToByte(int result, String dataId) {
        if (result > Byte.MAX_VALUE || result < Byte.MIN_VALUE) {
            ExtendedDatapacks.LOGGER.warn(
                    "[Arithmetic Instruction - Byte] The operation of type '{}', It has a result {}, which is out of range byte [{}, {}]. Truncated...",
                    dataId, result, Byte.MIN_VALUE, Byte.MAX_VALUE
            );
        }
        return (byte) Math.clamp(result, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
}