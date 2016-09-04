package to.uk.ekbkloz.gbhw27.proto;

import java.util.Collection;

/**
 * Created by Andrey on 03.09.2016.
 */
public class UsersList extends ProtoObject {
    private static final long serialVersionUID = -4610878952348955804L;
    private String chatRoom;
    private String[] list;

    public UsersList(String chatRoom, String[] list) {
        this.chatRoom = chatRoom;
        this.list = list;
    }

    public UsersList(String chatRoom, Collection<String> list) {
        this.chatRoom = chatRoom;
        this.list = list.toArray(new String[list.size()]);
    }

    public String[] getList() {
        return list;
    }

    public String getChatRoom() {
        return chatRoom;
    }
}
