package to.uk.ekbkloz.gbhw27.proto;

/**
 * Created by Andrey on 04.09.2016.
 */
public class JoinRoom extends ProtoObject {
    private static final long serialVersionUID = 4494174544059553662L;
    private final String roomName;

    public JoinRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }
}
