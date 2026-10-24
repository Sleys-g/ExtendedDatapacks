package sleys.efedp.forge.core.control;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.core.ArithmeticInstruction;

public record ArithmeticInstructionPair<N extends Number>(ArithmeticInstruction instruction, N amountValue) {

    public static <N extends Number> MapCodec<ArithmeticInstructionPair<N>> CODEC (PrimitiveCodec<N> numberCodec) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ArithmeticInstruction.CODEC.fieldOf("instruction")
                                .forGetter(ArithmeticInstructionPair::instruction),
                        numberCodec.fieldOf("amount_value")
                                .forGetter(ArithmeticInstructionPair::amountValue)
                ).apply(instance, ArithmeticInstructionPair::new)
        );
    }

    public byte getResult(byte current, byte operand) {
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }

    public int getResult(int current, int operand) {
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }

    public float getResult(float current, float operand) {
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }

    public double getResult(double current, double operand) {
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }

    public long getResult(long current, long operand) {
        return instruction.apply(current, operand, "Arithmetic Instruction Pair");
    }
}
