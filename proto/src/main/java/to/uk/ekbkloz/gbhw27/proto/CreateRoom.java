package to.uk.ekbkloz.gbhw27.proto;

/**
 * Created by Andrey on 04.09.2016.
 */
public class CreateRoom extends ProtoObject {
    private static final long serialVersionUID = -5315877192510989519L;
    private final String roomName;

    public CreateRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }
}
