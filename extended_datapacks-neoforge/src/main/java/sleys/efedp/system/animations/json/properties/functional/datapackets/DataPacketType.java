package sleys.efedp.system.animations.json.properties.functional.datapackets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.system.animations.json.properties.functional.datapackets.read.IDataRead;
import sleys.efedp.system.animations.json.properties.functional.datapackets.read.ReadArithmeticData;
import sleys.efedp.system.animations.json.properties.functional.datapackets.read.ReadLogicalData;
import sleys.efedp.system.animations.json.properties.functional.datapackets.read.ReadStringData;
import sleys.efedp.system.animations.json.properties.functional.datapackets.write.IDataWriter;
import sleys.efedp.system.animations.json.properties.functional.datapackets.write.WriteArithmeticData;
import sleys.efedp.system.animations.json.properties.functional.datapackets.write.WriteLogicalData;
import sleys.efedp.system.animations.json.properties.functional.datapackets.write.WriteStringData;
import sleys.sl.library.network.sync.TagSyncSender;
import sleys.sl.library.util.data.codec.EnumCodecs;

public enum DataPacketType {
    BYTE("byte", Codec.BYTE) {
        public void write(TagSyncSender.SyncMethod sync, LivingEntity livingEntity, String id, Object v) {
            TagSyncSender.sendByte(sync, livingEntity, id, (Byte) v);
        }

        public Byte read(CompoundTag tag, String id) {
            return tag.getByte(id);
        }

        @Override
        public MapCodec<? extends IDataWriter> writeCodec() {
            return WriteArithmeticData.codecFor(this);
        }

        @Override
        public MapCodec<? extends IDataRead> readCodec() {
            return ReadArithmeticData.codecFor(this);
        }
    },
    INT("int", Codec.INT) {
        public void write(TagSyncSender.SyncMethod sync, LivingEntity livingEntity, String id, Object v) {
            TagSyncSender.sendInt(sync, livingEntity, id, (Integer) v);
        }

        public Integer read(CompoundTag tag, String id) {
            return tag.getInt(id);
        }

        @Override
        public MapCodec<? extends IDataWriter> writeCodec() {
            return WriteArithmeticData.codecFor(this);
        }

        @Override
        public MapCodec<? extends IDataRead> readCodec() {
            return ReadArithmeticData.codecFor(this);
        }
    },
    FLOAT("float", Codec.FLOAT) {
        public void write(TagSyncSender.SyncMethod sync, LivingEntity livingEntity, String id, Object v) {
            TagSyncSender.sendFloat(sync, livingEntity, id, (Float) v);
        }

        public Float read(CompoundTag tag, String id) {
            return tag.getFloat(id);
        }

        @Override
        public MapCodec<? extends IDataWriter> writeCodec() {
            return WriteArithmeticData.codecFor(this);
        }

        @Override
        public MapCodec<? extends IDataRead> readCodec() {
            return ReadArithmeticData.codecFor(this);
        }
    },
    DOUBLE("double", Codec.DOUBLE) {
        public void write(TagSyncSender.SyncMethod sync, LivingEntity livingEntity, String id, Object v) {
            TagSyncSender.sendDouble(sync, livingEntity, id, (Double) v);
        }

        public Double read(CompoundTag tag, String id) {
            return tag.getDouble(id);
        }

        @Override
        public MapCodec<? extends IDataWriter> writeCodec() {
            return WriteArithmeticData.codecFor(this);
        }

        @Override
        public MapCodec<? extends IDataRead> readCodec() {
            return ReadArithmeticData.codecFor(this);
        }
    },
    LONG("long", Codec.LONG) {
        public void write(TagSyncSender.SyncMethod sync, LivingEntity livingEntity, String id, Object v) {
            TagSyncSender.sendLong(sync, livingEntity, id, (Long) v);
        }

        public Long read(CompoundTag tag, String id) {
            return tag.getLong(id);
        }

        @Override
        public MapCodec<? extends IDataWriter> writeCodec() {
            return WriteArithmeticData.codecFor(this);
        }

        @Override
        public MapCodec<? extends IDataRead> readCodec() {
            return ReadArithmeticData.codecFor(this);
        }
    },
    BOOL("bool", Codec.BOOL) {
        public void write(TagSyncSender.SyncMethod sync, LivingEntity livingEntity, String id, Object v) {
            TagSyncSender.sendBoolean(sync, livingEntity, id, (Boolean) v);
        }

        public Boolean read(CompoundTag tag, String id) {
            return tag.getBoolean(id);
        }

        @Override
        public MapCodec<? extends IDataWriter> writeCodec() {
            return WriteLogicalData.codecFor(this);
        }

        @Override
        public MapCodec<? extends IDataRead> readCodec() {
            return ReadLogicalData.codecFor(this);
        }
    },
    STRING("string", Codec.STRING) {
        public void write(TagSyncSender.SyncMethod sync, LivingEntity livingEntity, String id, Object v) {
            TagSyncSender.sendString(sync, livingEntity, id, (String) v);
        }

        public String read(CompoundTag tag, String id) {
            return tag.getString(id);
        }

        @Override
        public MapCodec<? extends IDataWriter> writeCodec() {
            return WriteStringData.codecFor(this);
        }

        @Override
        public MapCodec<? extends IDataRead> readCodec() {
            return ReadStringData.codecFor(this);
        }
    };

    private final String id;
    private final Codec<?> valueCodec;

    DataPacketType(String id, Codec<?> valueCodec) {
        this.id = id;
        this.valueCodec = valueCodec;
    }

    public abstract void write(TagSyncSender.SyncMethod sync, LivingEntity livingEntity, String dataId, Object value);
    public abstract Object read(CompoundTag tag, String dataId);
    public abstract MapCodec<? extends IDataWriter> writeCodec();
    public abstract MapCodec<? extends IDataRead> readCodec();

    public Codec<?> valueCodec() {
        return valueCodec;
    }

    @SuppressWarnings("unchecked")
    public Codec<Object> objectCodec() {
        return (Codec<Object>) valueCodec;
    }

    public String getSerializedName() {
        return id;
    }

    public static final Codec<DataPacketType> CODEC = EnumCodecs.byId(
            DataPacketType.values(), packet -> packet.id
    );
}