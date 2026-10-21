package sleys.efedp.neoforge.core.control;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.neoforge.core.ArithmeticInstruction;
import sleys.efedp.neoforge.core.datapackets.get.IDataGet;

public record ArithmeticInstructionDataPair(ArithmeticInstruction instruction, IDataGet dataValue) {

    public static final MapCodec<ArithmeticInstructionDataPair> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ArithmeticInstruction.CODEC.fieldOf("instruction")
                                    .forGetter(ArithmeticInstructionDataPair::instruction),
                            IDataGet.CODEC.fieldOf("data_value")
                                    .forGetter(ArithmeticInstructionDataPair::dataValue)
                    ).apply(instance, ArithmeticInstructionDataPair::new)
            );

    public byte getResult(byte current, LivingEntity entity) {
        var rawData = dataValue.readData(entity);
        byte operand = rawData instanceof Number num ? num.byteValue() : 1;
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }

    public int getResult(int current, LivingEntity entity) {
        var rawData = dataValue.readData(entity);
        var operand = rawData instanceof Number num ? num.intValue() : 1;
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }

    public float getResult(float current, LivingEntity entity) {
        var rawData = dataValue.readData(entity);
        var operand = rawData instanceof Number num ? num.floatValue() : 1;
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }

    public double getResult(double current, LivingEntity entity) {
        var rawData = dataValue.readData(entity);
        var operand = rawData instanceof Number num ? num.doubleValue() : 1;
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }

    public long getResult(long current, LivingEntity entity) {
        var rawData = dataValue.readData(entity);
        var operand = rawData instanceof Number num ? num.longValue() : 1;
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }
}