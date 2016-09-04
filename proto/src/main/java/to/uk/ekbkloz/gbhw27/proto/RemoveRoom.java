package to.uk.ekbkloz.gbhw27.proto;

/**
 * Created by Andrey on 04.09.2016.
 */
public class RemoveRoom extends ProtoObject {
    private static final long serialVersionUID = -5611150852795971342L;
    private final String roomName;

    public RemoveRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }
}
