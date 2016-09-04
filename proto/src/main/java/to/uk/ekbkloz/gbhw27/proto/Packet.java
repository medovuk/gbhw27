package to.uk.ekbkloz.gbhw27.proto;

import java.io.*;

/**
 * Created by Andrey on 03.09.2016.
 */
public class Packet implements Serializable {
    private static final long serialVersionUID = 4282621093773776764L;
    private final PacketType type;
    private final ProtoObject payload;

    public Packet(PacketType type, ProtoObject payload) {
        this.type = type;
        this.payload = payload;
    }

    public PacketType getType() {
        return type;
    }

    public ProtoObject getPayload() {
        return payload;
    }

    public <T> T getPayload(Class<T> clazz) throws ClassCastException {
        return clazz.cast(payload);
    }
}
