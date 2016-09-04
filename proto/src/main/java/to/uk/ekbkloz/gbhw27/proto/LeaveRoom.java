package to.uk.ekbkloz.gbhw27.proto;

/**
 * Created by Andrey on 05.09.2016.
 */
public class LeaveRoom extends ProtoObject {
    private static final long serialVersionUID = 7589182279855922586L;
    private final String roomName;

    public LeaveRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }
}
